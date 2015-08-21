package info.javaspec.spec;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.context.Context;
import info.javaspec.context.FakeContext;
import info.javaspec.dsl.It;
import info.javaspecproto.ContextClasses;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static info.javaspec.testutil.Assertions.assertNoThrow;
import static info.javaspec.testutil.Assertions.assertThrows;
import static info.javaspec.testutil.Matchers.matchesRegex;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.fail;
import static org.junit.runner.Description.createSuiteDescription;
import static org.mockito.Mockito.mock;

@RunWith(HierarchicalContextRunner.class)
public class SpecTest {
  private Spec subject;
  private final RunNotifier notifier = mock(RunNotifier.class);

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

        Spec pendingCleanup = SpecBuilder.exampleWith(ContextClasses.PendingCleanup.class, "asserts",
          newArrayList("arranges", "acts"), newArrayList("cleans"));
        assertThat(pendingCleanup.isIgnored(), equalTo(true));
      }
    }

    private void shouldBeSkipped(Class<?> contextClass, boolean isIgnored) {
      Spec subject = SpecBuilder.exampleWith(contextClass, "asserts", newArrayList("arranges", "acts"), newArrayList());
      assertThat(subject.isIgnored(), equalTo(isIgnored));
    }
  }

  public class run {
    public class givenAClassWithoutACallableNoArgConstructor {
      @Test
      public void notifiesTestFailureWithUnsupportedConstructor() throws Exception {
        Failure failure = reportedFailure(ContextClasses.ConstructorWithArguments.class, "is_otherwise_valid");
        assertThat(failure.getException(), instanceOf(UnsupportedConstructor.class));
        assertThat(failure.getException().getMessage(), matchesRegex("^Unable to find a no-argument constructor for class .*ConstructorWithArguments$"));
      }
    }

    public class givenAFaultyConstructor {
      @Test
      public void notifiesWithTestSetupFailed() throws Exception {
        assertTestSetupFailed(ContextClasses.FailingConstructor.class, "will_fail", InvocationTargetException.class);
      }
    }

    public class givenAFaultyInitializer {
      @Test
      public void notifiesWithTestSetupFailed() throws Exception {
        assertTestSetupFailed(ContextClasses.FailingClassInitializer.class, "will_fail", AssertionError.class);
      }
    }

    public class whenAFieldCanNotBeAccessed {
      @Test
      public void throwsTestSetupFailedCausedByReflectionError() {
        //Intended to catch ReflectiveOperationException, but causing that with a fake SecurityManager was not reliable
        Spec subject = SpecBuilder.exampleWithIt(HasWrongType.class, "inaccessibleAsIt");
        assertThrows(TestSetupFailed.class, startsWith("Failed to create test context"),
          ClassCastException.class, () -> subject.run());
      }
    }

    public class givenANonPublicContextClass {
      private final Spec subject = SpecBuilder.exampleWithIt(getHiddenClass(), "runs");

      @Test
      public void obtainsAccessToItsConstructor() throws Exception {
        subject.run();
      }

      private Class<?> getHiddenClass() {
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
    }

    public class givenFieldsAccessibleFromAContextClass {
      private final List<String> events = new LinkedList<String>();
      private final Spec subject = SpecBuilder.exampleWithFullFixture();

      @Before
      public void spy() throws Exception {
        ContextClasses.FullFixture.setEventListener(events::add);
        subject.run();
      }

      @After
      public void releaseSpy() {
        ContextClasses.FullFixture.setEventListener(null);
      }

      @Test
      public void instantiatesContextClassesThenRunsSetupThenAssertsThenCleansUp() throws Exception {
        assertThat(events, contains(
          "ContextClasses.FullFixture::new",
          "ContextClasses.FullFixture::arrange",
          "ContextClasses.FullFixture::act",
          "ContextClasses.FullFixture::assert",
          "ContextClasses.FullFixture::cleans"));
      }
    }

    public class givenANestedContext {
      @Test
      public void instantiatesTheNestedContextClasses() throws Exception {
        Spec subject = SpecBuilder.exampleWithIt(ContextClasses.NestedThreeDeep.middle.bottom.class, "asserts");
        assertNoThrow(subject::run);
      }

      @Test
      public void usesTheTreeOfContextObjectsToRunTheFixtureLambdas() throws Exception {
        Spec subject = SpecBuilder.exampleWithNestedFullFixture();
        assertNoThrow(subject::run);
      }
    }

    public class whenATestFunctionThrows {
      @Test
      public void throwsWhateverEstablishOrBecauseThrows() {
        Spec subject = SpecBuilder.exampleWith(ContextClasses.FailingEstablish.class, "will_never_run",
          newArrayList("flawed_setup"), newArrayList());
        assertThrows(UnsupportedOperationException.class, equalTo("flawed_setup"), subject::run);
      }

      @Test
      public void throwsWhateverItThrows() {
        Spec subject = SpecBuilder.exampleWithIt(ContextClasses.FailingIt.class, "fails");
        assertThrows(AssertionError.class, anything(), subject::run);
      }

      @Test
      public void throwsWhateverCleanupThrows() {
        Spec subject = SpecBuilder.exampleWith(ContextClasses.FailingCleanup.class, "may_run",
          newArrayList(), newArrayList("flawed_cleanup"));
        assertThrows(IllegalStateException.class, equalTo("flawed_cleanup"), subject::run);
      }
    }

    public class givenACleanupField {
      public class whenASetupActionOrAssertionFunctionThrows {
        private final List<String> events = new LinkedList<String>();
        private final Spec subject = SpecBuilder.exampleWith(ContextClasses.FailingEstablishWithCleanup.class, "it",
          newArrayList("establish"), newArrayList("cleanup"));
        private Throwable thrown;

        @Before
        public void spy() throws Exception {
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
      Spec subject = SpecBuilder.exampleWithIt(ConstructorWithSideEffects.class, "expects_to_be_run_once");
      subject.isIgnored();
      subject.run();
    }

    private void assertTestSetupFailed(Class<?> context, String itFieldName, Class<? extends Throwable> cause) {
      Failure value = reportedFailure(context, itFieldName);
      assertThat(value.getException(), instanceOf(TestSetupFailed.class));
      assertThat(value.getException().getMessage(), matchesRegex("^Failed to create test context .*$"));
      assertThat(value.getException().getCause(), instanceOf(cause));
    }

    private Failure reportedFailure(Class<?> context, String itFieldName) {
      Spec subject = SpecBuilder.exampleWithIt(context, itFieldName);
      RunNotifier notifier = mock(RunNotifier.class);
      subject.run(notifier);

      ArgumentCaptor<Failure> failureCaptor = ArgumentCaptor.forClass(Failure.class);
      Mockito.verify(notifier).fireTestFailure(failureCaptor.capture());
      return failureCaptor.getValue();
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
