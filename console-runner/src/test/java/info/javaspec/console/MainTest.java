package info.javaspec.console;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.Reporter;
import info.javaspec.RunObserver;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

@RunWith(HierarchicalContextRunner.class)
public class MainTest {
  public class runCommand {
    private Main subject;
    private Reporter reporter;
    private Main.ExitHandler system;
    private Command command;

    @Before
    public void setup() {
      this.reporter = Mockito.mock(Reporter.class);
      this.system = Mockito.mock(Main.ExitHandler.class);
      this.subject = new Main(this.reporter, this.system);
      this.command = Mockito.mock(Command.class);
    }

    @Test
    public void runsTheCommand() throws Exception {
      subject.runCommand(this.command);
      Mockito.verify(this.command, Mockito.times(1)).run(this.reporter);
    }

    @Test
    public void exitsWithTheExitCodeReturnedByTheCommand() throws Exception {
      Mockito.stub(this.command.run(this.reporter)).toReturn(42);
      subject.runCommand(this.command);
      Mockito.verify(this.system, Mockito.times(1)).exit(42);
    }
  }
}
