package info.javaspec.console;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.console.ArgumentParser.CommandFactory;
import info.javaspec.lang.lambda.InstanceSpecFinder;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.notNull;

@RunWith(HierarchicalContextRunner.class)
public class ArgumentParserTest {
  private Main.CommandParser subject;
  private CommandFactory commandFactory;

  public class parseCommand {
    @Before
    public void setup() throws Exception {
      commandFactory = Mockito.mock(CommandFactory.class);
      subject = new ArgumentParser(commandFactory);
    }

    @Test
    public void createsRunSpecsCommandWithAllArgsAsClassNames() throws Exception {
      Command command = Mockito.mock(Command.class);
      Mockito.when(commandFactory.runSpecsCommand(
        notNull(InstanceSpecFinder.class),
        org.mockito.Matchers.eq(Collections.singletonList("one")))
      ).thenReturn(command);

      Command returned = subject.parseCommand(Collections.singletonList("one"));
      assertThat(returned, Matchers.sameInstance(command));
    }
  }
}
