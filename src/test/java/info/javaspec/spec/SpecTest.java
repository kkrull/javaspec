package info.javaspec.spec;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.context.Context;
import info.javaspec.context.FakeContext;
import info.javaspec.dsl.It;
import info.javaspec.testutil.Matchers;
import info.javaspecproto.ContextClasses;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static info.javaspec.testutil.Assertions.assertNoThrow;
import static info.javaspec.testutil.Assertions.assertThrows;
import static info.javaspec.testutil.Matchers.isThrowableMatching;
import static info.javaspec.testutil.Matchers.matchesRegex;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.fail;
import static org.junit.runner.Description.createSuiteDescription;
import static org.mockito.Mockito.mock;

@RunWith(HierarchicalContextRunner.class)
public class SpecTest {
  private Spec subject;

  public class isIgnored {
    public class whenEachJavaSpecFieldHasAnAssignedValue {
      @Test
      public void returnsFalse() {
        shouldBeSkipped(ContextClasses.FullFixture.class, false);
      }
    }

    public class when1OrMoreJavaSpecFieldsDoNotHaveAnAssignedValue {
      @Test
      public void returnsTrue() {
        shouldBeSkipped(ContextClasses.PendingEstablish.class, true);
        shouldBeSkipped(ContextClasses.PendingBecause.class, true);
        shouldBeSkipped(ContextClasses.PendingIt.class, true);

        subject = SpecBuilder.forClass(ContextClasses.PendingCleanup.class)
          .withBeforeFieldsNamed("arranges", "acts")
          .withAfterFieldsNamed("cleans")
          .buildForItFieldNamed("asserts");
        assertThat(subject.isIgnored(), equalTo(true));
      }
    }

    private void shouldBeSkipped(Class<?> contextClass, boolean isIgnored) {
      subject = SpecBuilder.forClass(contextClass)
        .withBeforeFieldsNamed("arranges", "acts")
        .buildForItFieldNamed("asserts");
      assertThat(subject.isIgnored(), equalTo(isIgnored));
    }
  }

  public class run {
    private final RunNotifier notifier = mock(RunNotifier.class);

    public class itNotifiesTestFailureGiven {
      @Test
      public void aReflectiveOperationException() {
        //Intended to catch ReflectiveOperationException, but causing that with a fake SecurityManager was not reliable
        Failure failure = reportedFailure(runNotifications(HasWrongType.class, "inaccessibleAsIt"));
        assertThat(failure.getException(), isThrowableMatching(TestSetupFailed.class, "Failed to create test context .*"));
      }

      @Test
      public void aContextClassWithoutACallableNoArgConstructor() throws Exception {
        Failure failure = reportedFailure(runNotifications(ContextClasses.ConstructorWithArguments.class, "is_otherwise_valid"));
        assertThat(failure.getException(), Matchers.isThrowableMatching(UnsupportedConstructor.class,
          "^Unable to find a no-argument constructor for class .*ConstructorWithArguments$"));
      }

      @Test
      public void anExplodingConstructor() throws Exception {
        assertTestSetupFailed(ContextClasses.FailingConstructor.class, "will_fail", InvocationTargetException.class);
      }

      @Test
      public void anExplodingClassInitializer() throws Exception {
        assertTestSetupFailed(ContextClasses.FailingClassInitializer.class, "will_fail", AssertionError.class);
      }

      @Test @Ignore
      public void anExplodingEstablishLambda() {
        subject = SpecBuilder.forClass(ContextClasses.FailingEstablish.class)
          .withBeforeFieldsNamed("flawed_setup")
          .buildForItFieldNamed("will_never_run");
        assertThrows(UnsupportedOperationException.class, equalTo("flawed_setup"), () -> subject.run());
      }

      @Test
      public void anExplodingItLambda() {
        subject = SpecBuilder.forClass(ContextClasses.FailingIt.class).buildForItFieldNamed("fails");
        Failure failure = reportedFailure(subject);
        assertThat(failure.getException(), instanceOf(AssertionError.class));
      }

      @Test @Ignore
      public void anExplodingCleanupLambda() {
        subject = SpecBuilder.forClass(ContextClasses.FailingCleanup.class)
          .withAfterFieldsNamed("flawed_cleanup")
          .buildForItFieldNamed("may_run");
        assertThrows(IllegalStateException.class, equalTo("flawed_cleanup"), subject::run);
      }
    }

    public class givenANonPublicContextClass {
      @Test
      public void obtainsAccessToItsConstructor() throws Exception {
        subject = SpecBuilder.forClass(getHiddenClass()).buildForItFieldNamed("runs");
        subject.run(notifier);
      }
    }

    public class givenFieldsAccessibleFromAContextClass {
      private final List<String> events = new LinkedList<>();

      @Before
      public void spy() throws Exception {
        subject = getSpec(ContextClasses.FullFixture.class, "asserts");
        ContextClasses.FullFixture.setEventListener(events::add);
        subject.run(notifier);
      }

      @After
      public void releaseSpy() {
        ContextClasses.FullFixture.setEventListener(null);
      }

      @Test
      public void instantiatesContextClassesThenRunsSetupThenAssertsThenCleansUp() throws Exception {
        assertThat(events, equalTo(newArrayList(
          "ContextClasses.FullFixture::new",
          "ContextClasses.FullFixture::arrange",
          "ContextClasses.FullFixture::act",
          "ContextClasses.FullFixture::assert",
          "ContextClasses.FullFixture::cleans")));
      }
    }

    public class givenANestedContext {
      @Test
      public void instantiatesTheNestedContextClasses() throws Exception {
        subject = SpecBuilder
          .forClass(ContextClasses.NestedThreeDeep.middle.bottom.class)
          .buildForItFieldNamed("asserts");
        assertNoThrow(() -> subject.run(notifier));
      }

      @Test
      public void usesTheTreeOfContextObjectsToRunTheFixtureLambdas() throws Exception {
        subject = getSpec(ContextClasses.NestedFullFixture.innerContext.class, "asserts");
        assertNoThrow(() -> subject.run(notifier));
      }
    }

    public class givenACleanupField {
      public class whenASetupActionOrAssertionFunctionThrows {
        private final List<String> events = new LinkedList<>();
        private Throwable thrown;

        @Before
        public void spy() throws Exception {
          subject = SpecBuilder.forClass(ContextClasses.FailingEstablishWithCleanup.class)
            .withBeforeFieldsNamed("establish")
            .withAfterFieldsNamed("cleanup")
            .buildForItFieldNamed("it");

          ContextClasses.FailingEstablishWithCleanup.setEventListener(events::add);
          try {
            subject.run();
          } catch(Throwable t) {
            this.thrown = t;
          }
        }

        @After
        public void releaseSpy() {
          ContextClasses.FailingEstablishWithCleanup.setEventListener(null);
        }

        @Test
        public void runsTheCleanupInCaseAnyStatementsExecutedBeforeTheError() {
          assertThat(events, contains(
            "ContextClasses.FailingEstablishWithCleanup::establish",
            "ContextClasses.FailingEstablishWithCleanup::cleanup"));
        }

        @Test
        public void stillThrowsTheOriginalException() {
          assertThat(thrown.getMessage(), equalTo("flawed_setup"));
        }
      }
    }

    @Test //Issue 2: code run at instantiation may have undesired side effects if run a second time
    public void runsWithTheSameInstanceThatWasUsedToCheckIfTheTestIsSkipped() throws Exception {
      subject = SpecBuilder.forClass(ConstructorWithSideEffects.class).buildForItFieldNamed("expects_to_be_run_once");
      subject.isIgnored();
      subject.run();
    }

    private void assertTestSetupFailed(Class<?> context, String itFieldName, Class<? extends Throwable> cause) {
      Failure value = reportedFailure(runNotifications(context, itFieldName));
      assertThat(value.getException(), instanceOf(TestSetupFailed.class));
      assertThat(value.getException().getMessage(), matchesRegex("^Failed to create test context .*$"));
      assertThat(value.getException().getCause(), instanceOf(cause));
    }
  }

  private Spec getSpec(Class<?> declaringClass, String fieldName) {
    Context context = FakeContext.withDescription(createSuiteDescription(declaringClass));
    SpecFactory specFactory = new SpecFactory(context);
    return specFactory.create(SpecBuilder.readField(declaringClass, fieldName));
  }

  private static Failure reportedFailure(Spec spec) {
    return reportedFailure(runNotifications(spec));
  }

  private static RunNotifier runNotifications(Class<?> context, String itFieldName) {
    Spec subject = SpecBuilder.forClass(context).buildForItFieldNamed(itFieldName);
    return runNotifications(subject);
  }

  private static RunNotifier runNotifications(Spec subject) {
    RunNotifier notifier = mock(RunNotifier.class);
    subject.run(notifier);
    return notifier;
  }

  private static Failure reportedFailure(RunNotifier notifier) {
    ArgumentCaptor<Failure> captor = ArgumentCaptor.forClass(Failure.class);
    Mockito.verify(notifier).fireTestFailure(captor.capture());
    return captor.getValue();
  }

  private static Class<?> getHiddenClass() {
    Class<?> outer;
    try {
      outer = Class.forName("info.javaspecproto.HiddenContext");
      return outer.getDeclaredClasses()[0];
    } catch(ClassNotFoundException e) {
      e.printStackTrace();
      fail("Unable to set up test");
      return null;
    }
  }

  public static class HasWrongType {
    public Object inaccessibleAsIt = new Object();
  }

  public static final class ConstructorWithSideEffects {
    private static int _numTimesInitialized = 0;
    public ConstructorWithSideEffects() { _numTimesInitialized++; }
    It expects_to_be_run_once = () -> assertThat(_numTimesInitialized, equalTo(1));
  }
}
