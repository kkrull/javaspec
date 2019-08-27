package info.javaspec.console;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.console.PluggableArgumentParser.CommandParser;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(HierarchicalContextRunner.class)
public class PluggableArgumentParserTest {
  private Main.ArgumentParser subject;
  private CommandParser mainArguments;

  public class parseCommand {
    @Before
    public void setup() throws Exception {
      mainArguments = Mockito.mock(CommandParser.class);
      subject = new PluggableArgumentParser(mainArguments);
    }
    
    @Test
    public void parsesEmptyArgumentsAsTheMainCommand() throws Exception {
      Command mainCommand = Mockito.mock(Command.class);
      Mockito.when(mainArguments.parseCommand(Mockito.anyListOf(String.class)))
        .thenReturn(mainCommand);

      Command returned = subject.parseCommand(Collections.emptyList());
      Mockito.verify(mainArguments).parseCommand(Mockito.eq(Collections.emptyList()));
      assertThat(returned, Matchers.sameInstance(mainCommand));
    }
  }
}
