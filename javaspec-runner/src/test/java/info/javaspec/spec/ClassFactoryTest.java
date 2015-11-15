package info.javaspec.spec;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.context.Context;
import info.javaspec.context.ContextFactory;
import info.javaspec.dsl.It;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.notification.RunNotifier;

import static info.javaspec.testutil.Assertions.capture;
import static info.javaspec.testutil.Matchers.matchesRegex;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

@RunWith(HierarchicalContextRunner.class)
public class ClassFactoryTest {
  public class run {
    private final RunNotifier notifier = mock(RunNotifier.class);

    @Test
    public void itThrowsFaultyClassInitializer_givenAnExplodingClassInitializer() throws Exception {
      ClassFactory.FaultyClassInitializer ex = capture(ClassFactory.FaultyClassInitializer.class, () -> {
        Context context = ContextFactory.createRootContext(FailingClassInitializer.class);
        context.run(notifier);
      });

      assertThat(ex.getMessage(),
        matchesRegex("^Failed to load class .*FailingClassInitializer.* due to a faulty static initializer$"));
      assertThat(ex.getCause(), instanceOf(ExceptionInInitializerError.class));
    }
  }

  //Hide the class here to prevent the class loader from running before the test that uses it
  private static class FailingClassInitializer {
    static { explode(); }
    private static void explode() { throw new RuntimeException("Faulty class initializer"); }
    It will_fail = () -> assertEquals(1, 1);
  }
}
