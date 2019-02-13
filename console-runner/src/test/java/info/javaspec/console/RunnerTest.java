package info.javaspec.console;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.SpecReporter;
import info.javaspec.Suite;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

@RunWith(HierarchicalContextRunner.class)
public class RunnerTest {
  public class main {
    private final Suite suite = Mockito.mock(Suite.class);
    private final SpecReporter reporter = Mockito.mock(SpecReporter.class);
    private final ExitHandler system = Mockito.mock(ExitHandler.class);

    @Test
    public void runsTheSuiteWithTheGivenReporter() throws Exception {
      Runner.main(suite, reporter, system);
      Mockito.verify(suite).runSpecs(reporter);
    }

    @Test
    public void callsRunStartingOnReporter() throws Exception {
      Runner.main(suite, reporter, system);
      Mockito.verify(reporter).runStarting();
    }

    @Test
    public void callsRunFinishedOnReporter() throws Exception {
      Runner.main(suite, reporter, system);
      Mockito.verify(reporter).runFinished();
    }

    public class whenAllSpecsPass {
      @Test
      public void exitsWithCode0ToIndicateSuccess() throws Exception {
        Mockito.stub(reporter.hasFailingSpecs()).toReturn(false);
        Runner.main(suite, reporter, system);
        Mockito.verify(system).exit(0);
      }
    }

    public class whenOneOrMoreSpecsFail {
      @Test
      public void exitsWithCode1ToIndicateFailure() throws Exception {
        Mockito.stub(reporter.hasFailingSpecs()).toReturn(true);
        Runner.main(suite, reporter, system);
        Mockito.verify(system).exit(1);
      }
    }
  }
}
