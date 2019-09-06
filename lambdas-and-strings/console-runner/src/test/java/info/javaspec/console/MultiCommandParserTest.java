package info.javaspec.console;

import com.beust.jcommander.Parameter;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.console.Exceptions.CommandAlreadyAdded;
import info.javaspec.console.Exceptions.InvalidArguments;
import info.javaspec.console.MultiCommandParser.JCommanderParameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.fail;

@RunWith(HierarchicalContextRunner.class)
public class MultiCommandParserTest {
  private MultiCommandParser subject;
  private Command mainCommand = Mockito.mock(Command.class);

  public class addCliCommand {
    @Test
    public void returnsItselfForUseInABuilderPattern() throws Exception {
      subject = new MultiCommandParser(() -> mainCommand);
      assertThat(
        subject.addCliCommand("anyName", () -> Mockito.mock(Command.class)),
        sameInstance(subject)
      );
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
      @Test(expected = InvalidArguments.class)
      public void throwsAnException() throws Exception {
        JCommanderParameters mainParamsWithNoOptions = () -> mainCommand;
        subject = new MultiCommandParser(mainParamsWithNoOptions);
        subject.parseCommand(Collections.singletonList("--unknown-option"));
      }

      @Test
      public void identifiesTheInvalidUsage() throws Exception {
        try {
          JCommanderParameters mainParamsWithNoOptions = () -> mainCommand;
          subject = new MultiCommandParser(mainParamsWithNoOptions);
          subject.parseCommand(Collections.singletonList("--unknown-option"));
        } catch(InvalidArguments subject) {
          assertThat(subject.getMessage(), containsString("--unknown-option"));
          return;
        }

        fail("Expected InvalidArguments");
      }
    }

    public class givenInvalidArgumentsForANamedCommand {
      @Test(expected = InvalidArguments.class)
      public void throwsAnException() throws Exception {
        JCommanderParameters mainParamsWithNoOptions = () -> mainCommand;
        subject = new MultiCommandParser(mainParamsWithNoOptions);
        subject.addCliCommand("valid-command", () -> Mockito.mock(Command.class));
        subject.parseCommand(Arrays.asList("valid-command", "--unknown-option"));
      }

      @Test
      public void identifiesTheInvalidUsage() throws Exception {
        InvalidArguments subject = tryInvalidCommand()
          .orElseThrow(() -> new AssertionError("Expected exception"));
        assertThat(subject.getMessage(), containsString("--unknown-option"));
      }

      @Test
      public void identifiesTheAttemptedCliCommand() throws Exception {
        InvalidArguments subject = tryInvalidCommand()
          .orElseThrow(() -> new AssertionError("Expected exception"));
        assertThat(subject.getMessage(), containsString("valid-command"));
      }

      private Optional<InvalidArguments> tryInvalidCommand() {
        try {
          JCommanderParameters mainParamsWithNoOptions = () -> mainCommand;
          subject = new MultiCommandParser(mainParamsWithNoOptions);
          subject.addCliCommand("valid-command", () -> Mockito.mock(Command.class));
          subject.parseCommand(Arrays.asList("valid-command", "--unknown-option"));
        } catch(InvalidArguments subject) {
          return Optional.of(subject);
        }

        return Optional.empty();
      }
    }

    public class givenArgumentsMatchingTheMainCommand {
      private ParametersWithValidOption mainParameters;

      @Before
      public void setup() throws Exception {
        mainParameters = new ParametersWithValidOption(mainCommand);
        subject = new MultiCommandParser(mainParameters);
      }

      @Test
      public void returnsTheCommandParsedFromTheMainCommandParameters() throws Exception {
        Command returned = subject.parseCommand(Collections.singletonList("--valid-option"));
        assertThat(returned, sameInstance(mainCommand));
        assertThat(mainParameters.validOption, equalTo(true));
      }
    }

    public class givenArgumentsMatchingANamedCommand {
      private Command oneCommand = Mockito.mock(Command.class);

      @Test
      public void returnsTheCommandParsedFromTheNamedCommandParameters() throws Exception {
        subject = new MultiCommandParser(() -> mainCommand);
        subject.addCliCommand("do-one-thing", () -> oneCommand);

        Command returned = subject.parseCommand(Collections.singletonList("do-one-thing"));
        assertThat(returned, sameInstance(oneCommand));
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
