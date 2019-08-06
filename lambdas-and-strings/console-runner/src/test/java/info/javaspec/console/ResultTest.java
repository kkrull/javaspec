package info.javaspec.console;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

@RunWith(HierarchicalContextRunner.class)
public class ResultTest {
  private Result subject;

  public class reportTo {
    private Reporter reporter;

    @Before
    public void setup() throws Exception {
      this.reporter = Mockito.mock(Reporter.class);
    }

    @Test
    public void aResultWithOnlyAnExitCodeReportsNothing() throws Exception {
      subject = Result.success();
      subject.reportTo(this.reporter);
      Mockito.verifyNoMoreInteractions(reporter);
    }

    @Test
    public void aResultWithAnExceptionReportsAFailureWithThatException() throws Exception {
      RuntimeException failure = new RuntimeException("bang!");
      subject = Result.failure(1, failure);
      subject.reportTo(this.reporter);
      Mockito.verify(reporter).commandFailed(Mockito.same(failure));
    }
  }
}
