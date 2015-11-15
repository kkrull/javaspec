package info.javaspec.spec;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.context.Context;
import info.javaspec.context.FakeContext;
import info.javaspec.spec.ClassFactory.UnsupportedConstructor;
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
import java.util.LinkedList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static info.javaspec.junit.Descriptions.isTestDescription;
import static info.javaspec.testutil.Matchers.isThrowableMatching;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.*;
import static org.junit.runner.Description.createSuiteDescription;
import static org.mockito.Mockito.*;

@RunWith(HierarchicalContextRunner.class)
public class FieldSpecTest {
  private Spec subject;
  private final List<String> events = new LinkedList<>();

  public class addDescriptionTo {
    public class givenASuite {
      private Description suite;

      @Before
      public void setup() throws Exception {
        subject = getSpec(ContextClasses.OneIt.class, "only_test");
        suite = Description.createSuiteDescription(getClass());
        subject.addDescriptionTo(suite);
      }

      @Test
      public void addsItselfAsAChildInThatSuite() throws Exception {
        assertThat(suite.getChildren(), contains(isTestDescription()));
      }
    }
  }

  public class run {
    private final RunNotifier notifier = mock(RunNotifier.class);

    public class givenAContextClassWithAssignedSpecFieldsAccessibleFromThatClass {
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

      @Test
      public void instantiatesOnceEvenIfRunMultipleTimes() throws Exception {
        subject.run(notifier);
        List<String> filteredEvents = events.stream()
          .filter(x -> x.contains("new") || x.contains("act"))
          .collect(toList());
        assertThat(filteredEvents, equalTo(newArrayList(
          "ContextClasses.FullFixture::new",
          "ContextClasses.FullFixture::act",
          "ContextClasses.FullFixture::act"
        )));
      }
    }

    public class givenAContextClass {
      public class thatIsNotPublic {
        @Test
        public void obtainsAccessToItsConstructor() throws Exception {
          subject = getSpec(ContextClasses.hiddenClass(), "runs");
          subject.run(notifier);
        }
      }

      public class withoutACallableNoArgConstructor {
        @Test
        public void reportsFailure() throws Exception {
          Spec subject = getSpec(ContextClasses.ConstructorWithArguments.class, "is_otherwise_valid");
          Failure failure = reportedFailure(subject);
          assertThat(failure.getException(), instanceOf(TestSetupFailed.class));
          assertThat(failure.getException().getCause(), isThrowableMatching(UnsupportedConstructor.class,
            "^Unable to find a no-argument constructor for class .*ConstructorWithArguments$"));
        }
      }

      public class withUnassignedSpecFields {
        @Test
        public void firesTestIgnored() throws Exception {
          shouldBeIgnored(getSpec(ContextClasses.PendingEstablish.class, "asserts"));
          shouldBeIgnored(getSpec(ContextClasses.PendingBecause.class, "asserts"));
          shouldBeIgnored(getSpec(ContextClasses.PendingIt.class, "asserts"));
          shouldBeIgnored(getSpec(ContextClasses.PendingCleanup.class, "asserts"));
        }

        @Test
        public void evenOnRepeatedAttemptsToRun() throws Exception {
          subject = getSpec(ContextClasses.PendingEstablish.class, "asserts");
          subject.run(notifier);
          subject.run(notifier);
          verify(notifier, times(2)).fireTestIgnored(Mockito.any());
          Mockito.verifyNoMoreInteractions(notifier);
        }
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

    public class whenAContextClassConstructorThrows {
      @Test //Issue 5
      public void reportsFailure() {
        subject = getSpec(ContextClasses.FailingConstructor.class, "will_fail");
        Failure failure = reportedFailure(subject);
        assertThat(failure.getException(), isThrowableMatching(TestSetupFailed.class, "Failed to create test context .*"));
        assertThat(failure.getException().getCause(), isThrowableMatching(UnsupportedConstructor.class,
          "^Unable to find a no-argument constructor for class .*FailingConstructor$"));
      }
    }

    public class whenThereIsAReflectiveOperationException {
      @Test
      public void reportsFailure() throws Exception {
        //Intended to catch ReflectiveOperationException, but causing that with a fake SecurityManager was not reliable
        subject = getSpec(ContextClasses.WrongTypeField.class, "inaccessibleAsIt");
        Failure failure = reportedFailure(subject);
        assertThat(failure.getException(), isThrowableMatching(TestSetupFailed.class, "Failed to create test context .*"));
      }
    }

    public class whenASpecLambdaThrows {
      @Test
      public void reportsFailure() throws Exception {
        shouldReportFailureFromEstablish();
        shouldReportFailureFromIt();
        shouldReportFailureFromCleanup();
      }

      private void shouldReportFailureFromEstablish() {
        subject = getSpec(ContextClasses.FailingEstablish.class, "will_never_run");
        Failure failure = reportedFailure(subject);
        assertThat(failure.getException(), instanceOf(AssertionError.class));
      }

      private void shouldReportFailureFromIt() {
        subject = getSpec(ContextClasses.FailingIt.class, "fails");
        Failure failure = reportedFailure(subject);
        assertThat(failure.getException(), instanceOf(AssertionError.class));
      }

      private void shouldReportFailureFromCleanup() {
        subject = getSpec(ContextClasses.FailingCleanup.class, "may_run");
        Failure failure = reportedFailure(subject);
        assertThat(failure.getException(), instanceOf(AssertionError.class));
      }
    }

    @Test //Issue 2: code run at instantiation may have undesired side effects if run a second time
    public void runsWithTheSameInstanceThatWasUsedToCheckIfTheTestIsSkipped() throws Exception {
      subject = getSpec(ContextClasses.ConstructorWithSideEffects.class, "expects_to_be_run_once");
      subject.run(notifier);
      verify(notifier, never()).fireTestFailure(Mockito.any());
    }
  }

  private static Spec getSpec(Class<?> declaringClass, String fieldName) {
    Context context = FakeContext.withDescription(createSuiteDescription(declaringClass));
    SpecFactory specFactory = new SpecFactory(context);
    return specFactory.create(readField(declaringClass, fieldName));
  }

  private static Field readField(Class<?> declaringClass, String name) {
    try {
      return declaringClass.getDeclaredField(name);
    } catch(Exception e) {
      String message = String.format("Failed to read field %s from %s", name, declaringClass);
      throw new RuntimeException(message, e);
    }
  }

  private static void shouldBeIgnored(Spec subject) {
    RunNotifier notifier = mock(RunNotifier.class);
    subject.run(notifier);
    verify(notifier).fireTestIgnored(Mockito.any());
    Mockito.verifyNoMoreInteractions(notifier);
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
