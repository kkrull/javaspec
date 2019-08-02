package info.javaspec.console;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
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
      Mockito.stub(this.command.run()).toReturn(Command.Result.success());
      subject.runCommand(this.command);
      Mockito.verify(this.command, Mockito.times(1)).run();
    }

    @Test
    public void exitsWithTheExitCodeReturnedByTheCommand() throws Exception {
      Command.Result failure = Command.Result.failure(42, "...you're not going to like it.");
      Mockito.stub(this.command.run()).toReturn(failure);
      subject.runCommand(this.command);
      Mockito.verify(this.system, Mockito.times(1)).exit(42);
    }

    @Test
    public void reportsTheResult() throws Exception {
      Command.Result result = Mockito.mock(Command.Result.class);
      Mockito.stub(this.command.run()).toReturn(result);
      subject.runCommand(this.command);
      Mockito.verify(result).reportTo(Mockito.same(this.reporter));
    }
  }
}
