package info.javaspec.console;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.console.ArgumentParser.CommandFactory;
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
  private CommandFactory commandFactory;

  public class parseCommand {
    public class whenTheFirstArgumentIs_run {
      @Before
      public void setup() throws Exception {
        commandFactory = Mockito.mock(CommandFactory.class);
        subject = new ArgumentParser(commandFactory);
      }

      @Test
      public void createsRunSpecsCommandWithTheRestOfTheArgsAsClassNames() throws Exception {
        Command command = Mockito.mock(Command.class);
        Mockito.when(commandFactory.runSpecsCommand(
          Matchers.eq(Collections.singletonList("one")))
        ).thenReturn(command);

        Command returned = subject.parseCommand(Arrays.asList("run", "one"));
        assertThat(returned, sameInstance(command));
      }
    }
  }
}
