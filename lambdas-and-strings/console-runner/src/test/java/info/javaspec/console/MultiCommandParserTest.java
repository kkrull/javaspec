package info.javaspec.console;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.console.MultiCommandParser.JCommanderArguments;
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
  public class parseCommand {
    private MultiCommandParser subject;
    private Command mainExecutableCommand = Mockito.mock(Command.class);

    public class givenInvalidParametersForTheMainCommand {
      @Test(expected = ParameterException.class)
      public void throwsAnException() throws Exception {
        JCommanderArguments mainArgsWithNoOptions = () -> mainExecutableCommand;
        subject = new MultiCommandParser(mainArgsWithNoOptions);
        subject.parseCommand(Collections.singletonList("--unknown-option"));
      }
    }

    public class givenACommandWithInvalidParameters {
      @Test(expected = ParameterException.class)
      public void throwsAnException() throws Exception {
        JCommanderArguments mainArgsWithNoOptions = () -> mainExecutableCommand;
        subject = new MultiCommandParser(mainArgsWithNoOptions);
        subject.addCliCommand("valid-command", () -> Mockito.mock(Command.class));
        subject.parseCommand(Arrays.asList("valid-command", "--unknown-option"));
      }
    }

    public class givenOptionsMatchingTheMainCliCommand {
      @Before
      public void setup() throws Exception {
        JCommanderArguments mainArgsWithValidOption = new JCommanderArguments() {
          @Parameter(names = "--valid-option")
          public boolean validOption;

          @Override
          public Command toExecutableCommand() {
            return mainExecutableCommand;
          }
        };
        subject = new MultiCommandParser(mainArgsWithValidOption);
      }

      @Test
      public void parsesAllArgumentsAsMainArguments() throws Exception {
        Command returned = subject.parseCommand(Collections.singletonList("--valid-option"));
        assertThat(returned, Matchers.sameInstance(mainExecutableCommand));
      }
    }

    public class givenArgumentsMatchingACliCommand {
      private Command oneExecutableCommand = Mockito.mock(Command.class);

      @Before
      public void setup() throws Exception {
        JCommanderArguments main = () -> mainExecutableCommand;
        JCommanderArguments one = () -> oneExecutableCommand;
        subject = new MultiCommandParser(main);
        subject.addCliCommand("do-one-thing", one);
      }

      @Test
      public void parsesThatCommandAndItsArguments() throws Exception {
        Command returned = subject.parseCommand(Collections.singletonList("do-one-thing"));
        assertThat(returned, Matchers.sameInstance(oneExecutableCommand));
      }
    }
  }
}
