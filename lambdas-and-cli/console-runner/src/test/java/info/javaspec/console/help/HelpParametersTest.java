package info.javaspec.console.help;

import com.beust.jcommander.JCommander;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.console.Command;
import info.javaspec.console.CommandFactory;
import info.javaspec.console.JCommanderHelpers;
import info.javaspec.console.Reporter;
import info.javaspec.console.ReporterFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.sameInstance;

@RunWith(HierarchicalContextRunner.class)
public class HelpParametersTest {
  public class parseCommand {
    private HelpParameters subject;
    private CommandFactory commandFactory;
    private ReporterFactory reporterFactory;

    public class givenNoArguments {
      @Before
      public void setup() throws Exception {
        commandFactory = Mockito.mock(CommandFactory.class);
        reporterFactory = Mockito.mock(ReporterFactory.class);
        subject = new HelpParameters(commandFactory, reporterFactory);

        JCommanderHelpers.parseCommandArgs(subject, "help");
      }

      @Test
      public void returnsATopLevelHelpCommand() throws Exception {
        Command toCreate = Mockito.mock(Command.class);
        Mockito.when(commandFactory.helpCommand(
          Mockito.any(),
          Mockito.any(JCommander.class))
        ).thenReturn(toCreate);

        assertThat(subject.toExecutableCommand(anyJCommander()), sameInstance(toCreate));
      }

      @Test
      public void usesAPlaintextReporterAndTheGivenJCommander() throws Exception {
        Reporter reporter = Mockito.mock(Reporter.class);
        Mockito.when(reporterFactory.plainTextReporter())
          .thenReturn(reporter);

        JCommander jCommander = anyJCommander();
        subject.toExecutableCommand(jCommander);
        Mockito.verify(commandFactory).helpCommand(
          Mockito.same(reporter),
          Mockito.same(jCommander)
        );
      }
    }
  }

  private JCommander anyJCommander() {
    return Mockito.mock(JCommander.class);
  }
}
