package info.javaspec.console;

import com.beust.jcommander.JCommander;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.console.help.HelpObserver;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(HierarchicalContextRunner.class)
public class MainParametersTest {
  private MainParameters subject;
  private CommandFactory commandFactory = Mockito.mock(CommandFactory.class);
  private ReporterFactory reporterFactory = Mockito.mock(ReporterFactory.class);

  public class toExecutableCommand {
    private Command returned;
    private Command helpCommand = Mockito.mock(Command.class);
    private Reporter plainTextReporter = Mockito.mock(Reporter.class);

    @Before
    public void setup() throws Exception {
      subject = new MainParameters(commandFactory, reporterFactory);
      Mockito.stub(commandFactory.helpCommand(Mockito.any(HelpObserver.class)))
        .toReturn(helpCommand);
      Mockito.stub(reporterFactory.plainTextReporter())
        .toReturn(plainTextReporter);
    }

    @Test
    public void usesAPlainTextReporter() throws Exception {
      JCommanderHelpers.parseMainArgs(subject);
      returned = subject.toExecutableCommand(anyJCommander());
      Mockito.verify(commandFactory).helpCommand(Mockito.same(plainTextReporter));
    }

    public class givenNoOptions {
      @Test
      public void returnsAHelpCommand() throws Exception {
        JCommanderHelpers.parseMainArgs(subject);
        returned = subject.toExecutableCommand(anyJCommander());
        assertThat(returned, Matchers.sameInstance(helpCommand));
      }
    }

    public class givenAHelpOption {
      @Test
      public void returnsAHelpCommand() throws Exception {
        JCommanderHelpers.parseMainArgs(subject, "--help");
        returned = subject.toExecutableCommand(anyJCommander());
        assertThat(returned, Matchers.sameInstance(helpCommand));
      }
    }
  }

  private JCommander anyJCommander() {
    return Mockito.mock(JCommander.class);
  }
}
