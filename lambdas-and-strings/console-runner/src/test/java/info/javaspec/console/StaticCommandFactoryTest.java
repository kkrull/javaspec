package info.javaspec.console;

import com.beust.jcommander.JCommander;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.RunObserver;
import info.javaspec.console.help.DetailedHelpCommand;
import info.javaspec.console.help.HelpCommand;
import info.javaspec.console.help.HelpObserver;
import info.javaspec.lang.lambda.RunSpecsCommand;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.net.URL;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;

@RunWith(HierarchicalContextRunner.class)
public class StaticCommandFactoryTest {
  private CommandFactory subject;

  @Before
  public void setup() throws Exception {
    subject = new StaticCommandFactory();
  }

  public class helpCommand {
    @Test
    public void returnsHelpCommandForNoSpecificCommand() throws Exception {
      Command command = subject.helpCommand(
        Mockito.mock(HelpObserver.class),
        Mockito.mock(JCommander.class)
      );
      assertThat(command, instanceOf(HelpCommand.class));
      command.run();
    }

    @Test
    public void returnsDetailedHelpCommandForASpecifiedCommand() throws Exception {
      Command command = subject.helpCommand(Mockito.mock(HelpObserver.class), "run");
      assertThat(command, instanceOf(DetailedHelpCommand.class));
    }
  }

  public class runSpecsCommand {
    @Test
    public void returnsRunSpecsCommandWithTheGivenClasses() throws Exception {
      Command command = subject.runSpecsCommand(
        Mockito.mock(RunObserver.class),
        new URL("file:/specs.jar"),
        Collections.emptyList()
      );
      assertThat(command, instanceOf(RunSpecsCommand.class));
    }
  }
}
