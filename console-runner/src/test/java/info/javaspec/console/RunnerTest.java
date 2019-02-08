package info.javaspec.console;

import info.javaspec.Spec;
import info.javaspec.SpecReporter;
import info.javaspec.Suite;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

public class RunnerTest {

  @Test
  public void runsTheSuiteWithTheGivenReporter() throws Exception {
    Suite suite = Mockito.mock(Suite.class);
    SpecReporter reporter = Mockito.mock(SpecReporter.class);
    Runner.main(suite, reporter, null);
    Mockito.verify(suite).runSpecs(reporter);
  }

  @Test
  public void shouldCallRunStartingOnReporter() throws Exception {
    Suite suite = Mockito.mock(Suite.class);
    SpecReporter reporter = Mockito.mock(SpecReporter.class);
    Runner.main(suite, reporter, null);
    Mockito.verify(reporter).runStarting();
  }

  @Test
  public void shouldCallRunFinishedOnReporter() throws Exception {
    Suite suite = Mockito.mock(Suite.class);
    SpecReporter reporter = Mockito.mock(SpecReporter.class);
    Runner.main(suite, reporter, null);
    Mockito.verify(reporter).runFinished();
  }

}
