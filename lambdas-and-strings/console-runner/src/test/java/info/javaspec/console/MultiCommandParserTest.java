package info.javaspec.console;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.console.MultiCommandParser.JCommanderArguments;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(HierarchicalContextRunner.class)
public class MultiCommandParserTest {
  public class parseCommand {
    private Main.ArgumentParser subject;
    private Command mainReturns = Mockito.mock(Command.class);

    public class givenArgumentsThatDoNotMatchAnyCommand {
      @Test(expected = ParameterException.class)
      public void throwsAnException() throws Exception {
        JCommanderArguments main = Mockito.mock(JCommanderArguments.class);
        subject = new MultiCommandParser(main);
        subject.parseCommand(Collections.singletonList("--unknown-option"));
      }
    }

    public class givenOptionsMatchingTheMainCommand {
      @Before
      public void setup() throws Exception {
        JCommanderArguments mainWithValidOption = new JCommanderArguments() {
          @Parameter(names = "--valid-option")
          public boolean validOption;

          @Override
          public Command toExecutableCommand() {
            return mainReturns;
          }
        };
        subject = new MultiCommandParser(mainWithValidOption);
      }

      @Test
      public void parsesAllArgumentsAsMainArguments() throws Exception {
        Command returned = subject.parseCommand(Collections.singletonList("--valid-option"));
        assertThat(returned, Matchers.sameInstance(mainReturns));
      }
    }
  }
}
