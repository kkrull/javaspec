package info.javaspec.console;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.console.Exceptions.CommandAlreadyAdded;
import info.javaspec.console.Exceptions.InvalidArguments;
import info.javaspec.console.MultiCommandParser.JCommanderParameters;
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
      subject = new MultiCommandParser(anyExecutableName(), parametersReturning(mainCommand));
      assertThat(
        subject.addCliCommand("anyName", parametersReturning(Mockito.mock(Command.class))),
        sameInstance(subject)
      );
    }

    @Test(expected = CommandAlreadyAdded.class)
    public void throwsWhenAddingTheSameCommandTwice() throws Exception {
      JCommanderParameters duplicateParameters = parametersReturning(Mockito.mock(Command.class));

      subject = new MultiCommandParser(anyExecutableName(), parametersReturning(mainCommand));
      subject.addCliCommand("duplicate-command", duplicateParameters);
      subject.addCliCommand("duplicate-command", duplicateParameters);
    }
  }

  public class parseCommand {
    public class givenInvalidArgumentsForTheMainCommand {
      @Test(expected = InvalidArguments.class)
      public void throwsAnException() throws Exception {
        JCommanderParameters mainParamsWithNoOptions = parametersReturning(mainCommand);
        subject = new MultiCommandParser(anyExecutableName(), mainParamsWithNoOptions);
        subject.parseCommand(Collections.singletonList("--unknown-option"));
      }

      @Test
      public void identifiesTheInvalidUsage() throws Exception {
        try {
          JCommanderParameters mainParamsWithNoOptions = parametersReturning(mainCommand);
          subject = new MultiCommandParser(anyExecutableName(), mainParamsWithNoOptions);
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
        JCommanderParameters mainParamsWithNoOptions = parametersReturning(mainCommand);
        subject = new MultiCommandParser(anyExecutableName(), mainParamsWithNoOptions);
        subject.addCliCommand("valid-command", parametersReturning(Mockito.mock(Command.class)));
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
          JCommanderParameters mainParamsWithNoOptions = parametersReturning(mainCommand);
          subject = new MultiCommandParser(anyExecutableName(), mainParamsWithNoOptions);
          subject.addCliCommand("valid-command", parametersReturning(Mockito.mock(Command.class)));
          subject.parseCommand(Arrays.asList("valid-command", "--unknown-option"));
        } catch(InvalidArguments subject) {
          return Optional.of(subject);
        }

        return Optional.empty();
      }
    }

    public class givenArgumentsMatchingTheMainCommand {
      @Test
      public void returnsTheCommandParsedFromTheMainCommandParameters() throws Exception {
        ParametersWithValidOption mainParameters = new ParametersWithValidOption(mainCommand);
        subject = new MultiCommandParser(anyExecutableName(), mainParameters);

        Command returned = subject.parseCommand(Collections.singletonList("--valid-option"));
        assertThat(returned, sameInstance(mainCommand));
        assertThat(mainParameters.validOption, equalTo(true));
      }

      @Test
      public void passesTheJCommanderObject() throws Exception {
        JCommanderParameters mainParameters = Mockito.mock(JCommanderParameters.class);
        subject = new MultiCommandParser(anyExecutableName(), mainParameters);

        subject.parseCommand(Collections.emptyList());
        Mockito.verify(mainParameters).toExecutableCommand(Mockito.notNull(JCommander.class));
      }
    }

    public class givenArgumentsMatchingANamedCommand {
      private Command oneCommand = Mockito.mock(Command.class);

      @Test
      public void returnsTheCommandParsedFromTheNamedCommandParameters() throws Exception {
        subject = new MultiCommandParser(anyExecutableName(), parametersReturning(mainCommand));
        subject.addCliCommand("do-one-thing", parametersReturning(oneCommand));

        Command returned = subject.parseCommand(Collections.singletonList("do-one-thing"));
        assertThat(returned, sameInstance(oneCommand));
      }
    }
  }

  private String anyExecutableName() {
    return "";
  }

  private JCommanderParameters parametersReturning(Command command) {
    return parser -> command;
  }

  private static final class ParametersWithValidOption implements JCommanderParameters {
    private final Command command;

    public ParametersWithValidOption(Command returningCommand) {
      this.command = returningCommand;
    }

    @Parameter(names = "--valid-option")
    public boolean validOption;

    @Override
    public Command toExecutableCommand(JCommander jCommander) {
      return this.command;
    }
  }
}
