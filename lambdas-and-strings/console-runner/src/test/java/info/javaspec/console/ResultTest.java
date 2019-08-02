package info.javaspec.console;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

@RunWith(HierarchicalContextRunner.class)
public class ResultTest {
  private Command.Result subject;

  public class reportTo {
    private Reporter reporter;

    @Before
    public void setup() throws Exception {
      this.reporter = Mockito.mock(Reporter.class);
    }

    @Test
    public void aResultWithOnlyAnExitCodeReportsNothing() throws Exception {
      subject = Command.Result.success();
      subject.reportTo(this.reporter);
      Mockito.verify(reporter, Mockito.never()).commandFailed(Mockito.any(Exception.class));
    }
  }
}
