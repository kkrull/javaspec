package info.javaspec.console.help;

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

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.sameInstance;

@RunWith(HierarchicalContextRunner.class)
public class HelpArgumentsTest {
  public class parseCommand {
    private HelpArguments subject;
    private CommandFactory commandFactory;
    private ReporterFactory reporterFactory;

    public class givenNoArguments {
      @Before
      public void setup() throws Exception {
        commandFactory = Mockito.mock(CommandFactory.class);
        reporterFactory = Mockito.mock(ReporterFactory.class);
        subject = new HelpArguments(commandFactory, reporterFactory);

        JCommanderHelpers.parseCommandArgs(subject, "help");
      }

      @Test
      public void returnsATopLevelHelpCommand() throws Exception {
        Command toCreate = Mockito.mock(Command.class);
        Mockito.when(commandFactory.helpCommand(Mockito.any()))
          .thenReturn(toCreate);

        assertThat(subject.toExecutableCommand(), sameInstance(toCreate));
      }

      @Test
      public void usesAPlaintextReporter() throws Exception {
        Reporter reporter = Mockito.mock(Reporter.class);
        Mockito.when(reporterFactory.plainTextReporter())
          .thenReturn(reporter);

        subject.toExecutableCommand();
        Mockito.verify(commandFactory).helpCommand(Mockito.same(reporter));
      }
    }

    public class givenTheNameOfAnotherCommand {
      @Before
      public void setup() throws Exception {
        commandFactory = Mockito.mock(CommandFactory.class);
        reporterFactory = Mockito.mock(ReporterFactory.class);
        subject = new HelpArguments(commandFactory, reporterFactory);

        JCommanderHelpers.parseCommandArgs(subject, "help", "world-peace");
      }

      @Test
      public void returnsAHelpCommandForTheSpecifiedCommand() throws Exception {
        Command toCreate = Mockito.mock(Command.class);
        Mockito.when(
          commandFactory.helpCommand(
            Mockito.any(),
            Mockito.eq("world-peace"))
        ).thenReturn(toCreate);

        assertThat(subject.toExecutableCommand(), sameInstance(toCreate));
      }

      @Test
      public void usesAPlaintextReporter() throws Exception {
        Reporter reporter = Mockito.mock(Reporter.class);
        Mockito.when(reporterFactory.plainTextReporter())
          .thenReturn(reporter);

        subject.toExecutableCommand();
        Mockito.verify(commandFactory).helpCommand(
          Mockito.same(reporter),
          Mockito.anyString()
        );
      }
    }
  }
}
