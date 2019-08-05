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
      this.subject = new Main(reporter, system);
      this.command = Mockito.mock(Command.class);
    }

    @Test
    public void runsTheCommand() throws Exception {
      Mockito.stub(command.run()).toReturn(Result.success());
      subject.runCommand(command);
      Mockito.verify(command, Mockito.times(1)).run();
    }

    @Test
    public void exitsWithTheExitCodeReturnedByTheCommand() throws Exception {
      Result failure = Result.failure(42, "...you're not going to like it.");
      Mockito.stub(command.run()).toReturn(failure);
      subject.runCommand(command);
      Mockito.verify(system, Mockito.times(1)).exit(42);
    }

    @Test
    public void reportsTheResult() throws Exception {
      Result result = Mockito.mock(Result.class);
      Mockito.stub(command.run()).toReturn(result);
      subject.runCommand(command);
      Mockito.verify(result).reportTo(Mockito.same(reporter));
    }
  }
}
