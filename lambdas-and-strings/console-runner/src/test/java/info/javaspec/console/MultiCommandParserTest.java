package info.javaspec.console;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.console.MultiCommandParser.JCommanderParameters;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(HierarchicalContextRunner.class)
public class MultiCommandParserTest {
  public class parseCommand {
    private MultiCommandParser subject;
    private Command mainCommand = Mockito.mock(Command.class);

    public class givenInvalidParametersForTheMainCommand {
      @Test(expected = ParameterException.class)
      public void throwsAnException() throws Exception {
        JCommanderParameters mainParamsWithNoOptions = () -> mainCommand;
        subject = new MultiCommandParser(mainParamsWithNoOptions);
        subject.parseCommand(Collections.singletonList("--unknown-option"));
      }
    }

    public class givenACommandWithInvalidParameters {
      @Test(expected = ParameterException.class)
      public void throwsAnException() throws Exception {
        JCommanderParameters mainParamsWithNoOptions = () -> mainCommand;
        subject = new MultiCommandParser(mainParamsWithNoOptions);
        subject.addCliCommand("valid-command", () -> Mockito.mock(Command.class));
        subject.parseCommand(Arrays.asList("valid-command", "--unknown-option"));
      }
    }

    public class givenOptionsMatchingTheMainCliCommand {
      private ParametersWithValidOption mainParameters;

      @Before
      public void setup() throws Exception {
        mainParameters = new ParametersWithValidOption(mainCommand);
        subject = new MultiCommandParser(mainParameters);
      }

      @Test
      public void parsesAllArgumentsAsMainArguments() throws Exception {
        Command returned = subject.parseCommand(Collections.singletonList("--valid-option"));
        assertThat(returned, Matchers.sameInstance(mainCommand));
        assertThat(mainParameters.validOption, Matchers.equalTo(true));
      }
    }

    public class givenArgumentsMatchingACliCommand {
      private Command oneCommand = Mockito.mock(Command.class);

      @Before
      public void setup() throws Exception {
        JCommanderParameters main = () -> mainCommand;
        JCommanderParameters one = () -> oneCommand;
        subject = new MultiCommandParser(main);
        subject.addCliCommand("do-one-thing", one);
      }

      @Test
      public void parsesThatCommandAndItsArguments() throws Exception {
        Command returned = subject.parseCommand(Collections.singletonList("do-one-thing"));
        assertThat(returned, Matchers.sameInstance(oneCommand));
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
