package info.javaspec.console;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.console.ArgumentParser.CommandFactory;
import info.javaspec.console.ArgumentParser.InvalidCommand;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.sameInstance;

@RunWith(HierarchicalContextRunner.class)
public class ArgumentParserTest {
  private Main.CommandParser subject;
  private CommandFactory factory;

  @Before
  public void setup() throws Exception {
    factory = Mockito.mock(CommandFactory.class);
    subject = new ArgumentParser(factory);
  }

  public class parseCommand {
    public class givenNoArguments {
      @Test
      public void returnsHelpCommand() throws Exception {
        Command helpCommand = Mockito.mock(Command.class);
        Mockito.when(factory.helpCommand())
          .thenReturn(helpCommand);

        Command returned = subject.parseCommand(Collections.emptyList());
        assertThat(returned, sameInstance(helpCommand));
      }
    }

    public class givenHelp {
      @Test
      public void returnsHelpCommand() throws Exception {
        Command helpCommand = Mockito.mock(Command.class);
        Mockito.when(factory.helpCommand())
          .thenReturn(helpCommand);

        Command returned = subject.parseCommand(Collections.singletonList("help"));
        assertThat(returned, sameInstance(helpCommand));
      }
    }

    public class givenRunAndZeroOrMoreClassNames {
      @Test
      public void createsRunSpecsCommandWithNoClassNames() throws Exception {
        Command runCommand = Mockito.mock(Command.class);
        Mockito.when(factory.runSpecsCommand(Matchers.anyListOf(String.class)))
          .thenReturn(runCommand);

        Command returned = subject.parseCommand(Arrays.asList("run"));
        Mockito.verify(factory).runSpecsCommand(
          Matchers.eq(Collections.emptyList()));
        assertThat(returned, sameInstance(runCommand));
      }
    }

    public class givenRunAndOneOrMoreClassNames {
      @Test
      public void createsRunSpecsCommandWithTheRestOfTheArgsAsClassNames() throws Exception {
        Command runCommand = Mockito.mock(Command.class);
        Mockito.when(factory.runSpecsCommand(Matchers.anyListOf(String.class)))
          .thenReturn(runCommand);

        Command returned = subject.parseCommand(Arrays.asList("run", "one"));
        Mockito.verify(factory).runSpecsCommand(
          Matchers.eq(Collections.singletonList("one")));
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

  public class invalidCommand {
    @Test
    public void reportsTheInvalidCommand() throws Exception {
      Exception command = InvalidCommand.named("oracle");
      assertThat(command.getMessage(), equalTo("Invalid command: oracle"));
    }
  }
}
