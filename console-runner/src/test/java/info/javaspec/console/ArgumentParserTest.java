package info.javaspec.console;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.RunObserver;
import info.javaspec.console.ArgumentParser.CommandFactory;
import info.javaspec.console.ArgumentParser.InvalidCommand;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.sameInstance;

@RunWith(HierarchicalContextRunner.class)
public class ArgumentParserTest {
  private Main.CommandParser subject;
  private CommandFactory factory;
  private Reporter reporter;

  @Before
  public void setup() throws Exception {
    factory = Mockito.mock(CommandFactory.class);
    reporter = Mockito.mock(Reporter.class);
    subject = new ArgumentParser(factory, reporter);
  }

  public class parseCommand {
    public class givenNoArguments {
      @Test
      public void returnsHelpCommandWithTheReporter() throws Exception {
        Command helpCommand = Mockito.mock(Command.class);
        Mockito.when(factory.helpCommand(Mockito.any()))
          .thenReturn(helpCommand);

        Command returned = subject.parseCommand(Collections.emptyList());
        assertThat(returned, sameInstance(helpCommand));
        Mockito.verify(factory).helpCommand(Mockito.same(reporter));
      }
    }

    public class givenHelpWithNoArguments {
      @Test
      public void returnsHelpCommandWithTheReporter() throws Exception {
        Command helpCommand = Mockito.mock(Command.class);
        Mockito.when(factory.helpCommand(Mockito.any()))
          .thenReturn(helpCommand);

        Command returned = subject.parseCommand(Collections.singletonList("help"));
        assertThat(returned, sameInstance(helpCommand));
        Mockito.verify(factory).helpCommand(Mockito.same(reporter));
      }
    }

    public class givenHelpWithACommand {
      @Test
      public void returnsHelpCommandForTheSpecifiedCommand() throws Exception {
        Command helpCommand = Mockito.mock(Command.class);
        Mockito.when(factory.helpCommand(Mockito.any(), Mockito.anyString()))
          .thenReturn(helpCommand);

        Command returned = subject.parseCommand(Arrays.asList("help", "run"));
        assertThat(returned, sameInstance(helpCommand));
        Mockito.verify(factory).helpCommand(Mockito.same(reporter), Mockito.eq("run"));
      }
    }

    public class givenRunWithNoArguments {
      @Test(expected = InvalidCommand.class)
      public void throwsAnError() throws Exception {
        subject.parseCommand(Arrays.asList("run"));
      }
    }

    public class givenRunWithoutAReporterOption {
      @Test(expected = InvalidCommand.class)
      public void throwsAnError() throws Exception {
        subject.parseCommand(Arrays.asList("run", "one"));
      }
    }

    public class givenAValidRunCommandWithZeroOrMoreClassNames {
      @Test
      public void createsRunSpecsCommandWithNoClassNames() throws Exception {
        Command runCommand = Mockito.mock(Command.class);
        Mockito.when(
          factory.runSpecsCommand(
            Matchers.any(RunObserver.class),
            Matchers.anyListOf(String.class))
        ).thenReturn(runCommand);

        Command returned = subject.parseCommand(Arrays.asList("run", "--reporter=plaintext"));
        Mockito.verify(factory).runSpecsCommand(
          Matchers.same(reporter),
          Matchers.eq(Collections.emptyList())
        );
        assertThat(returned, sameInstance(runCommand));
      }
    }

    public class givenAValidRunCommandAndOneOrMoreClassNames {
      @Test
      public void createsRunSpecsCommandWithTheRestOfTheArgsAsClassNames() throws Exception {
        Command runCommand = Mockito.mock(Command.class);
        Mockito.when(
          factory.runSpecsCommand(
            Matchers.any(RunObserver.class),
            Matchers.anyListOf(String.class))
        ).thenReturn(runCommand);

        Command returned = subject.parseCommand(Arrays.asList("run", "--reporter=plaintext", "one"));
        Mockito.verify(factory).runSpecsCommand(
          Matchers.same(reporter),
          Matchers.eq(Collections.singletonList("one"))
        );
        assertThat(returned, sameInstance(runCommand));
      }
    }

    public class givenAnythingElse {
      @Test(expected = InvalidCommand.class)
      public void throwsAnError() throws Exception {
        subject.parseCommand(Collections.singletonList("bogus"));
      }
    }
  }
}
