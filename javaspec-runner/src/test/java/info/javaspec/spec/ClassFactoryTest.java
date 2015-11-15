package info.javaspec.spec;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.context.Context;
import info.javaspec.context.ContextFactory;
import info.javaspec.dsl.It;
import info.javaspec.spec.ClassFactory.FaultyClassInitializer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.mockito.ArgumentCaptor;

import static info.javaspec.testutil.Matchers.matchesRegex;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(HierarchicalContextRunner.class)
public class ClassFactoryTest {
  public class run {
    private final RunNotifier notifier = mock(RunNotifier.class);

    public class whenThereIsAFailureToLoadTheContextClass {
      @Before
      public void setup() throws Exception {
        Context context = ContextFactory.createRootContext(FailingClassInitializer.class);
        context.run(notifier);
      }

      @Test
      public void reportsFailureForTheContext() throws Exception {
        ArgumentCaptor<Failure> captor = ArgumentCaptor.forClass(Failure.class);
        verify(notifier).fireTestFailure(captor.capture());

        assertThat(captor.getValue().getException(), instanceOf(TestSetupFailed.class));
        assertThat(captor.getValue().getException().getCause(), instanceOf(FaultyClassInitializer.class));
        assertThat(captor.getValue().getException().getCause().getMessage(),
          matchesRegex("^Failed to load class .*FailingClassInitializer.* due to a faulty static initializer$"));
      }
    }
  }

  //Hide the class here to prevent the class loader from running before the test that uses it
  private static class FailingClassInitializer {
    static { explode(); }
    private static void explode() { throw new RuntimeException("Faulty class initializer"); }
    It will_fail = () -> assertEquals(1, 1);
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
