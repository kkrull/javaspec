package info.javaspec.console;

import com.beust.jcommander.JCommander;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.RunObserver;
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
  private StaticCommandFactory subject;

  @Before
  public void setup() throws Exception {
    subject = new StaticCommandFactory();
  }

  public class helpCommand {
    @Test
    public void returnsAHelpCommand() throws Exception {
      Command command = subject.helpCommand(
        Mockito.mock(HelpObserver.class),
        Mockito.mock(JCommander.class)
      );
      assertThat(command, instanceOf(HelpCommand.class));
      command.run();
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

    @Test
    public void returnsRunSpecsCommandWithTheGivenClassesFromTheGivenSources() throws Exception {
      Command command = subject.runSpecsCommand(
        Mockito.mock(RunObserver.class),
        Collections.singletonList(new URL("file:/specs.jar")),
        Collections.emptyList()
      );
      assertThat(command, instanceOf(RunSpecsCommand.class));
    }
  }
}
