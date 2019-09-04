package info.javaspec.console;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.console.Exceptions.CommandAlreadyAdded;
import info.javaspec.console.MultiCommandParser.JCommanderParameters;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(HierarchicalContextRunner.class)
public class MultiCommandParserTest {
  private MultiCommandParser subject;
  private Command mainCommand = Mockito.mock(Command.class);

  public class addCliCommand {
    public class givenArgumentsMatchingACliCommand {
      private Command oneCommand = Mockito.mock(Command.class);

      @Test
      public void addsACliCommandWithTheGivenNameAndParameters() throws Exception {
        subject = new MultiCommandParser(() -> mainCommand);
        subject.addCliCommand("do-one-thing", () -> oneCommand);

        Command returned = subject.parseCommand(Collections.singletonList("do-one-thing"));
        assertThat(returned, Matchers.sameInstance(oneCommand));
      }
    }

    @Test(expected = CommandAlreadyAdded.class)
    public void throwsWhenAddingTheSameCommandTwice() throws Exception {
      JCommanderParameters duplicateParameters = () -> Mockito.mock(Command.class);

      subject = new MultiCommandParser(() -> mainCommand);
      subject.addCliCommand("duplicate-command", duplicateParameters);
      subject.addCliCommand("duplicate-command", duplicateParameters);
    }
  }

  public class parseCommand {
    public class givenInvalidArgumentsForTheMainCommand {
      @Test(expected = ParameterException.class)
      public void throwsAnException() throws Exception {
        JCommanderParameters mainParamsWithNoOptions = () -> mainCommand;
        subject = new MultiCommandParser(mainParamsWithNoOptions);
        subject.parseCommand(Collections.singletonList("--unknown-option"));
      }

      @Test @Ignore
      public void identifiesTheInvalidUsage() throws Exception {
      }
    }

    public class givenInvalidArgumentsForANamedCommand {
      @Test(expected = ParameterException.class)
      public void throwsAnException() throws Exception {
        JCommanderParameters mainParamsWithNoOptions = () -> mainCommand;
        subject = new MultiCommandParser(mainParamsWithNoOptions);
        subject.addCliCommand("valid-command", () -> Mockito.mock(Command.class));
        subject.parseCommand(Arrays.asList("valid-command", "--unknown-option"));
      }

      @Test @Ignore
      public void identifiesTheInvalidUsage() throws Exception {
      }

      @Test @Ignore
      public void identifiesTheAttemptedCliCommand() throws Exception {
      }
    }

    public class givenArgumentsMatchingAnyCliCommand {
      private ParametersWithValidOption mainParameters;

      @Before
      public void setup() throws Exception {
        mainParameters = new ParametersWithValidOption(mainCommand);
        subject = new MultiCommandParser(mainParameters);
      }

      @Test
      public void returnsTheExecutableCommandParsedFromTheSpecifiedParameters() throws Exception {
        Command returned = subject.parseCommand(Collections.singletonList("--valid-option"));
        assertThat(returned, Matchers.sameInstance(mainCommand));
        assertThat(mainParameters.validOption, Matchers.equalTo(true));
      }
    }
  }

  private static final class ParametersWithValidOption implements JCommanderParameters {
    private final Command command;

    public ParametersWithValidOption(Command returningCommand) {
      this.command = returningCommand;
    }

    @Parameter(names = "--valid-option")
    public boolean validOption;

    @Override
    public Command toExecutableCommand() {
      return this.command;
    }
  }
}
