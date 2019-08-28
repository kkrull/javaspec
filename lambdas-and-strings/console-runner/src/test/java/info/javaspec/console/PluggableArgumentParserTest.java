package info.javaspec.console;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.console.PluggableArgumentParser.CommandArguments;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(HierarchicalContextRunner.class)
public class PluggableArgumentParserTest {
  private Main.ArgumentParser subject;
  private CommandArguments mainParser;

  public class parseCommand {
    @Before
    public void setup() throws Exception {
      mainParser = Mockito.mock(CommandArguments.class);
    }

    public class givenArgumentsThatMatchAnArgumentParser {
      @Test
      public void returnsTheCommandFromTheMatchingCommandArguments() throws Exception {
        Command oneCommand = Mockito.mock(Command.class);
        CommandArguments oneParser = Mockito.mock(CommandArguments.class);
        Mockito.when(oneParser.makeCommand()).thenReturn(oneCommand);

        subject = new PluggableArgumentParser(mainParser, oneParser);
        Command returned = subject.parseCommand(Collections.singletonList("one"));
        Mockito.verify(oneParser).makeCommand();
        assertThat(returned, Matchers.sameInstance(oneCommand));
      }
    }

    @Test @Ignore
    public void parsesEmptyArgumentsAsTheMainCommand() throws Exception {
      Command mainCommand = Mockito.mock(Command.class);
      Mockito.when(mainParser.makeCommand())
        .thenReturn(mainCommand);

      subject = new PluggableArgumentParser(mainParser);
      Command returned = subject.parseCommand(Collections.emptyList());
      assertThat(returned, Matchers.sameInstance(mainCommand));
    }
  }
}
