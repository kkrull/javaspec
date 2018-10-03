package info.javaspec;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

@RunWith(HierarchicalContextRunner.class)
public class ConsoleRunnerTest {
  public class main {
    private SpecObserver reporter;
    private SpecSuite suite;
    private ExitHandler system;

    @Before
    public void setup() throws Exception {
      reporter = Mockito.mock(SpecObserver.class);
      system = Mockito.mock(ExitHandler.class);

      suite = Mockito.mock(SpecSuite.class);
      Mockito.stub(suite.run(Mockito.any(SpecObserver.class)))
        .toReturn(SuiteResult.ALL_SPECS_PASSED);
    }

    @Test
    public void firesTestRunStarted() throws Exception {
      ConsoleRunner.main(suite, reporter, system);
      Mockito.verify(reporter).testRunStarted();
    }

    @Test
    public void runsTheSuiteWithTheReporter() throws Exception {
      ConsoleRunner.main(suite, reporter, system);
      Mockito.verify(suite).run(reporter);
    }

    @Test
    public void firesTestRunFinished() throws Exception {
      ConsoleRunner.main(suite, reporter, system);
      Mockito.verify(reporter).testRunFinished();
    }

    public class whenAllSpecsPass {
      @Before
      public void setup() throws Exception {
        Mockito.stub(suite.run(Mockito.any(SpecObserver.class)))
          .toReturn(SuiteResult.ALL_SPECS_PASSED);
      }

      @Test
      public void exitsWithCodeZero() throws Exception {
        ConsoleRunner.main(suite, reporter, system);
        Mockito.verify(system).exit(0);
      }
    }

    public class whenOneOrMoreSpecsFail {
      @Before
      public void setup() throws Exception {
        Mockito.stub(suite.run(Mockito.any(SpecObserver.class)))
          .toReturn(SuiteResult.ONE_OR_MORE_SPECS_FAILED);
      }

      @Test
      public void exitsWithAPositiveCode() throws Exception {
        ConsoleRunner.main(suite, reporter, system);
        Mockito.verify(system).exit(Mockito.intThat(Matchers.greaterThan(0)));
      }
    }
  }
}
