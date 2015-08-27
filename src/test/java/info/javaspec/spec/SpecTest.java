package info.javaspec.spec;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.context.Context;
import info.javaspec.context.FakeContext;
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
import static info.javaspec.testutil.Assertions.capture;
import static info.javaspec.testutil.Matchers.isThrowableMatching;
import static info.javaspec.testutil.Matchers.matchesRegex;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.fail;
import static org.junit.runner.Description.createSuiteDescription;
import static org.mockito.Mockito.*;

@RunWith(HierarchicalContextRunner.class)
public class SpecTest {
  private Spec subject;
  private final List<String> events = new LinkedList<>();

  public class run {
    private final RunNotifier notifier = mock(RunNotifier.class);

    public class itFiresTestIgnored_given {
      @Test
      public void anUnassignedEstablishField() throws Exception {
        shouldBeIgnored(getSpec(ContextClasses.PendingEstablish.class, "asserts"));
      }

      @Test
      public void anUnassignedBecauseField() throws Exception {
        shouldBeIgnored(getSpec(ContextClasses.PendingBecause.class, "asserts"));
      }

      @Test
      public void anUnassignedItField() {
        shouldBeIgnored(getSpec(ContextClasses.PendingIt.class, "asserts"));
      }

      @Test
      public void anUnassignedCleanupField() throws Exception {
        shouldBeIgnored(getSpec(ContextClasses.PendingCleanup.class, "asserts"));
      }
    }

    public class itThrowsUnsupportedConstructorGiven {
      @Test
      public void aContextClassWithoutACallableNoArgConstructor() throws Exception {
        UnsupportedConstructor ex = capture(UnsupportedConstructor.class, () ->
          getSpec(ContextClasses.ConstructorWithArguments.class, "is_otherwise_valid"));
        assertThat(ex.getMessage(), matchesRegex(
          "^Unable to find a no-argument constructor for class .*ConstructorWithArguments$"));
      }
    }

    public class itNotifiesTestFailureGiven {
      @Test
      public void aReflectiveOperationException() {
        //Intended to catch ReflectiveOperationException, but causing that with a fake SecurityManager was not reliable
        subject = getSpec(ContextClasses.WrongTypeField.class, "inaccessibleAsIt");
        Failure failure = reportedFailure(subject);
        assertThat(failure.getException(), isThrowableMatching(TestSetupFailed.class, "Failed to create test context .*"));
      }

      @Test
      public void anExplodingConstructor() throws Exception {
        assertTestSetupFailed(ContextClasses.FailingConstructor.class, "will_fail", InvocationTargetException.class);
      }

      @Test
      public void anExplodingClassInitializer() throws Exception {
        assertTestSetupFailed(ContextClasses.FailingClassInitializer.class, "will_fail", AssertionError.class);
      }

      @Test
      public void anExplodingEstablishLambda() {
        subject = getSpec(ContextClasses.FailingEstablish.class, "will_never_run");
        Failure failure = reportedFailure(subject);
        assertThat(failure.getException(), instanceOf(AssertionError.class));
      }

      @Test
      public void anExplodingItLambda() {
        subject = getSpec(ContextClasses.FailingIt.class, "fails");
        Failure failure = reportedFailure(subject);
        assertThat(failure.getException(), instanceOf(AssertionError.class));
      }

      @Test
      public void anExplodingCleanupLambda() {
        subject = getSpec(ContextClasses.FailingCleanup.class, "may_run");
        Failure failure = reportedFailure(subject);
        assertThat(failure.getException(), instanceOf(AssertionError.class));
      }
    }

    public class givenANonPublicContextClass {
      @Test
      public void obtainsAccessToItsConstructor() throws Exception {
        subject = getSpec(ContextClasses.hiddenClass(), "runs");
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

    public class givenANestedContextWithBeforeSpecLambdasAtMultipleLevels {
      @Before
      public void setup() throws Exception {
        subject = getSpec(ContextClasses.NestedEstablish.innerContext.class, "asserts");
        ContextClasses.NestedEstablish.setEventListener(events::add);
        subject.run(notifier);
      }

      @After
      public void releaseSpy() {
        ContextClasses.NestedEstablish.setEventListener(null);
      }

      @Test
      public void runsBeforeSpecLambdasOutsideInBeforeTheAssertion() throws Exception {
        assertThat(events, equalTo(newArrayList(
          "ContextClasses.NestedEstablish::new",
          "ContextClasses.NestedEstablish.innerContext::new",
          "ContextClasses.NestedEstablish::arranges",
          "ContextClasses.NestedEstablish::innerContext::arranges",
          "ContextClasses.NestedEstablish.innerContext::asserts"
        )));
      }
    }

    public class givenANestedContextWithAfterSpecLambdasAtMultipleLevels {
      @Before
      public void setup() throws Exception {
        subject = getSpec(ContextClasses.NestedCleanup.innerContext.class, "asserts");
        ContextClasses.NestedEstablish.setEventListener(events::add);
        subject.run(notifier);
      }

      @After
      public void releaseSpy() {
        ContextClasses.NestedCleanup.setEventListener(null);
      }

      @Test
      public void runsAfterSpecLambdasInsideOutAfterTheAssertion() throws Exception {
        assertThat(events, equalTo(newArrayList(
          "ContextClasses.NestedCleanup::new",
          "ContextClasses.NestedCleanup.innerContext::new",
          "ContextClasses.NestedCleanup.innerContext::asserts",
          "ContextClasses.NestedCleanup::innerContext::cleans",
          "ContextClasses.NestedCleanup::cleans"
        )));
      }
    }

    public class givenACleanupField {
      public class whenASetupActionOrAssertionFunctionThrows {
        @Before
        public void spy() throws Exception {
          subject = getSpec(ContextClasses.FailingEstablishWithCleanup.class, "it");
          ContextClasses.FailingEstablishWithCleanup.setEventListener(events::add);
          subject.run(notifier);
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
        public void firesFailureWithTheOriginalException() {
          Failure failure = reportedFailure(notifier);
          assertThat(failure.getException().getMessage(), equalTo("flawed_setup"));
        }
      }
    }

    @Ignore
    @Test //Issue 2: code run at instantiation may have undesired side effects if run a second time
    public void runsWithTheSameInstanceThatWasUsedToCheckIfTheTestIsSkipped() throws Exception {
      subject = getSpec(ContextClasses.ConstructorWithSideEffects.class, "expects_to_be_run_once");
      subject.run(notifier);
      verify(notifier, never()).fireTestFailure(Mockito.any());
    }
  }

  private static void assertTestSetupFailed(Class<?> context, String itFieldName, Class<? extends Throwable> cause) {
    Spec subject = SpecBuilder.forClass(context).buildForItFieldNamed(itFieldName);
    Failure value = reportedFailure(runNotifications(subject));
    assertThat(value.getException(), instanceOf(TestSetupFailed.class));
    assertThat(value.getException().getMessage(), matchesRegex("^Failed to create test context .*$"));
    assertThat(value.getException().getCause(), instanceOf(cause));
  }

  private static void shouldBeIgnored(Spec subject) {
    RunNotifier notifier = mock(RunNotifier.class);
    subject.run(notifier);
    verify(notifier).fireTestIgnored(Mockito.any());
    Mockito.verifyNoMoreInteractions(notifier);
  }

  private Spec getSpec(Class<?> declaringClass, String fieldName) {
    Context context = FakeContext.withDescription(createSuiteDescription(declaringClass));
    SpecFactory specFactory = new SpecFactory(context);
    return specFactory.create(SpecBuilder.readField(declaringClass, fieldName));
  }

  private static Failure reportedFailure(Spec spec) {
    return reportedFailure(runNotifications(spec));
  }

  private static Failure reportedFailure(RunNotifier notifier) {
    ArgumentCaptor<Failure> captor = ArgumentCaptor.forClass(Failure.class);
    verify(notifier).fireTestFailure(captor.capture());
    return captor.getValue();
  }

  private static RunNotifier runNotifications(Spec subject) {
    RunNotifier notifier = mock(RunNotifier.class);
    subject.run(notifier);
    return notifier;
  }
}
