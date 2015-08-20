package info.javaspec.spec;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.context.Context;
import info.javaspec.context.FakeContext;
import info.javaspec.dsl.It;
import info.javaspecproto.ContextClasses;
import org.hamcrest.Matchers;
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

        Spec pendingCleanup = exampleWith(ContextClasses.PendingCleanup.class, "asserts",
          newArrayList("arranges", "acts"), newArrayList("cleans"));
        assertThat(pendingCleanup.isIgnored(), equalTo(true));
      }
    }

    private void shouldBeSkipped(Class<?> contextClass, boolean isIgnored) {
      Spec subject = exampleWith(contextClass, "asserts", newArrayList("arranges", "acts"), newArrayList());
      assertThat(subject.isIgnored(), equalTo(isIgnored));
    }
  }

  public class run {
    private Spec subject;
    private final RunNotifier notifier = mock(RunNotifier.class);
    private final ArgumentCaptor<Failure> failureCaptor = ArgumentCaptor.forClass(Failure.class);

    public class givenAClassWithoutACallableNoArgConstructor {
      @Before
      public void setup() throws Exception {
        subject = exampleWithIt(ContextClasses.ConstructorWithArguments.class, "is_otherwise_valid");
        subject.run(notifier);
      }

      @Test
      public void notifiesTestFailureWithUnsupportedConstructor() throws Exception {
        Mockito.verify(notifier).fireTestFailure(failureCaptor.capture());
        Failure value = failureCaptor.getValue();
        assertThat(value.getException(), instanceOf(UnsupportedConstructor.class));
        assertThat(value.getException().getMessage(), matchesRegex("^Unable to find a no-argument constructor for class .*ConstructorWithArguments$"));
      }
    }

    public class givenAFaultyConstructorOrInitializer {
      @Test
      public void notifiesWithTestSetupFailed() throws Exception {
        assertTestSetupFailed(ContextClasses.FailingConstructor.class, "will_fail", InvocationTargetException.class);
        assertTestSetupFailed(ContextClasses.FailingClassInitializer.class, "will_fail", AssertionError.class);
      }

      private void assertTestSetupFailed(Class<?> context, String itFieldName, Class<? extends Throwable> cause) {
        assertThrows(TestSetupFailed.class,
          is(String.format("Failed to create test context %s", context.getName())),
          cause, () -> {
            subject = exampleWithIt(context, itFieldName);
            subject.run(notifier);
          });
      }
    }

    public class whenAFieldCanNotBeAccessed {
      @Test
      public void throwsTestSetupFailedCausedByReflectionError() {
        //Intended to catch ReflectiveOperationException, but causing that with a fake SecurityManager was not reliable
        Spec subject = exampleWithIt(HasWrongType.class, "inaccessibleAsIt");
        assertThrows(TestSetupFailed.class, startsWith("Failed to create test context"),
          ClassCastException.class, subject::run);
      }
    }

    public class givenANonPublicContextClass {
      private final Spec subject = exampleWithIt(getHiddenClass(), "runs");

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
      private final Spec subject = exampleWithFullFixture();

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
        Spec subject = exampleWithIt(ContextClasses.NestedThreeDeep.middle.bottom.class, "asserts");
        assertNoThrow(subject::run);
      }

      @Test
      public void usesTheTreeOfContextObjectsToRunTheFixtureLambdas() throws Exception {
        Spec subject = exampleWithNestedFullFixture();
        assertNoThrow(subject::run);
      }
    }

    public class whenATestFunctionThrows {
      @Test
      public void throwsWhateverEstablishOrBecauseThrows() {
        Spec subject = exampleWith(ContextClasses.FailingEstablish.class, "will_never_run",
          newArrayList("flawed_setup"), newArrayList());
        assertThrows(UnsupportedOperationException.class, equalTo("flawed_setup"), subject::run);
      }

      @Test
      public void throwsWhateverItThrows() {
        Spec subject = exampleWithIt(ContextClasses.FailingIt.class, "fails");
        assertThrows(AssertionError.class, anything(), subject::run);
      }

      @Test
      public void throwsWhateverCleanupThrows() {
        Spec subject = exampleWith(ContextClasses.FailingCleanup.class, "may_run",
          newArrayList(), newArrayList("flawed_cleanup"));
        assertThrows(IllegalStateException.class, equalTo("flawed_cleanup"), subject::run);
      }
    }

    public class givenACleanupField {
      public class whenASetupActionOrAssertionFunctionThrows {
        private final List<String> events = new LinkedList<String>();
        private final Spec subject = exampleWith(ContextClasses.FailingEstablishWithCleanup.class, "it",
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
      Spec subject = exampleWithIt(ConstructorWithSideEffects.class, "expects_to_be_run_once");
      subject.isIgnored();
      subject.run();
    }
  }

  private static Spec exampleWithFullFixture() {
    return exampleWith(ContextClasses.FullFixture.class, "asserts",
      newArrayList("arranges", "acts"), newArrayList("cleans"));
  }

  private static Spec exampleWithIt(Class<?> context, String name) {
    return exampleWith(context, name, newArrayList(), newArrayList());
  }

  private static Spec exampleWithNestedFullFixture() {
    Context context = FakeContext.withDescription(createSuiteDescription(ContextClasses.NestedFullFixture.class));
    SpecFactory specFactory = new SpecFactory(context);
    return specFactory.create(readField(ContextClasses.NestedFullFixture.innerContext.class, "asserts"));
  }

  private static Spec exampleWith(Class<?> contextClass, String it, List<String> befores, List<String> afters) {
    try {
      return new FieldSpec(
        it,
        Description.createTestDescription(contextClass, it),
        it == null ? null : readField(contextClass, it),
        befores.stream().map(x -> readField(contextClass, x)).collect(toList()),
        afters.stream().map(x -> readField(contextClass, x)).collect(toList()));
    } catch(Exception e) {
      throw new RuntimeException("Test setup failed", e);
    }
  }

  private static Field readField(Class<?> context, String name) {
    try {
      return context.getDeclaredField(name);
    } catch(Exception e) {
      throw new RuntimeException(e);
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
