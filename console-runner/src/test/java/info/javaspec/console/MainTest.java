package info.javaspec.console;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.console.Main.Command;
import info.javaspec.console.Main.CommandParser;
import info.javaspec.console.Main.ExitHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

@RunWith(HierarchicalContextRunner.class)
public class MainTest {
  public class parseAndRunCommand {
    private Main subject;
    private CommandParser parser;
    private ExitHandler system;

    private Command command;

    @Before
    public void setup() {
      this.parser = Mockito.mock(CommandParser.class);
      this.system = Mockito.mock(ExitHandler.class);
      this.subject = new Main(this.parser, this.system);

      this.command = Mockito.mock(Command.class);
      Mockito.stub(this.parser.parseCommand(Mockito.any()))
        .toReturn(this.command);
    }

    @Test
    public void parsesTheCommandLine() throws Exception {
      subject.parseAndRunCommand("try", "to", "take", "over", "the", "world");
      Mockito.verify(parser).parseCommand(new String[]{ "try", "to", "take", "over", "the", "world" });
    }

    @Test
    public void runsTheReturnedCommand() throws Exception {
      subject.parseAndRunCommand();
      Mockito.verify(this.command, Mockito.times(1)).run();
    }

    @Test
    public void exitsWithTheExitCodeReturnedByTheCommand() throws Exception {
      Mockito.stub(this.command.run()).toReturn(42);
      subject.parseAndRunCommand();
      Mockito.verify(this.system, Mockito.times(1)).exit(42);
    }
  }
}
