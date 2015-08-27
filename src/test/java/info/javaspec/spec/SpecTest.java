package info.javaspec.spec;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.context.Context;
import info.javaspec.context.FakeContext;
import info.javaspec.dsl.It;
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

import java.lang.reflect.Field;
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
import static org.junit.Assert.assertEquals;
import static org.junit.runner.Description.createSuiteDescription;
import static org.mockito.Mockito.*;

@RunWith(HierarchicalContextRunner.class)
public class SpecTest {
  private Spec subject;
  private final List<String> events = new LinkedList<>();

  public class run {
    private final RunNotifier notifier = mock(RunNotifier.class);

    @Test
    public void itThrowsFaultyClassInitializer_givenAnExplodingClassInitializer() throws Exception {
      Context context = FakeContext.withDescription(createSuiteDescription("FailingClassInitializer"));
      FaultyClassInitializer ex = capture(FaultyClassInitializer.class, () -> {
        SpecFactory specFactory = new SpecFactory(context);
        specFactory.addSpecsFromClass(FailingClassInitializer.class);
      });

      assertThat(ex.getMessage(),
        matchesRegex("^Failed to load class .*FailingClassInitializer.* due to a faulty static initializer$"));
      assertThat(ex.getCause(), instanceOf(ExceptionInInitializerError.class));
    }

    public class itThrowsUnsupportedConstructor_given {
      @Test
      public void aContextClassWithoutACallableNoArgConstructor() throws Exception {
        UnsupportedConstructor ex = capture(UnsupportedConstructor.class, () ->
          getSpec(ContextClasses.ConstructorWithArguments.class, "is_otherwise_valid"));
        assertThat(ex.getMessage(), matchesRegex(
          "^Unable to find a no-argument constructor for class .*ConstructorWithArguments$"));
      }

      @Test
      public void anExplodingConstructor() throws Exception {
        UnsupportedConstructor ex = capture(UnsupportedConstructor.class, () ->
          getSpec(ContextClasses.FailingConstructor.class, "will_fail"));
        assertThat(ex, instanceOf(UnsupportedConstructor.class));
        assertThat(ex.getCause(), instanceOf(InvocationTargetException.class));
      }
    }

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

    @Test
    public void givenANonPublicContextClass_obtainsAccessToItsConstructor() throws Exception {
      subject = getSpec(ContextClasses.hiddenClass(), "runs");
      subject.run(notifier);
    }

    @Ignore
    @Test //Issue 2: code run at instantiation may have undesired side effects if run a second time
    public void runsWithTheSameInstanceThatWasUsedToCheckIfTheTestIsSkipped() throws Exception {
      subject = getSpec(ContextClasses.ConstructorWithSideEffects.class, "expects_to_be_run_once");
      subject.run(notifier);
      verify(notifier, never()).fireTestFailure(Mockito.any());
    }

    public class itNotifiesTestFailure_given {
      @Test
      public void aReflectiveOperationException() {
        //Intended to catch ReflectiveOperationException, but causing that with a fake SecurityManager was not reliable
        subject = getSpec(ContextClasses.WrongTypeField.class, "inaccessibleAsIt");
        Failure failure = reportedFailure(subject);
        assertThat(failure.getException(), isThrowableMatching(TestSetupFailed.class, "Failed to create test context .*"));
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
  }

  private static void shouldBeIgnored(Spec subject) {
    RunNotifier notifier = mock(RunNotifier.class);
    subject.run(notifier);
    verify(notifier).fireTestIgnored(Mockito.any());
    Mockito.verifyNoMoreInteractions(notifier);
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

  //Hide the class here to prevent the class loader from running before the test that uses it
  private static class FailingClassInitializer {
    static { explode(); }
    private static void explode() { throw new RuntimeException("Faulty class initializer"); }
    It will_fail = () -> assertEquals(1, 1);
  }
}
