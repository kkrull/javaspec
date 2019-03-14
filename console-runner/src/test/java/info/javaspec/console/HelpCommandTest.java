package info.javaspec.console;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.RunObserver;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@RunWith(HierarchicalContextRunner.class)
public class HelpCommandTest {
  private Command subject;

  public class run {
    private RunObserver observer;

    @Before
    public void setup() throws Exception {
      subject = new HelpCommand();
      observer = Mockito.mock(RunObserver.class);
    }

    @Test
    public void returns0() throws Exception {
      int status = subject.run();
      assertThat(status, equalTo(0));
    }

    @Test @Ignore
    public void writesUsageWithTheGivenObserver() throws Exception {
    }
  }
}
