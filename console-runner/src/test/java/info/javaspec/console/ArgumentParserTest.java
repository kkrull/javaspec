package info.javaspec.console;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.console.ArgumentParser.RunSpecsCommandFactory;
import info.javaspec.lang.lambda.InstanceSpecFinder;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.*;

@RunWith(HierarchicalContextRunner.class)
public class ArgumentParserTest {
  private Main.CommandParser subject;
  private RunSpecsCommandFactory newRunSpecsCommand;

  public class parseCommand {
    @Before
    public void setup() throws Exception {
      newRunSpecsCommand = Mockito.mock(RunSpecsCommandFactory.class);
      subject = new ArgumentParser(newRunSpecsCommand);
    }

    @Test
    public void createsRunSpecsCommandWithAllArgsAsClassNames() throws Exception {
      Command fromFactory = Mockito.mock(Command.class);
      Mockito.doReturn(fromFactory).when(newRunSpecsCommand)
        .make(
          notNull(InstanceSpecFinder.class),
          org.mockito.Matchers.eq(Collections.singletonList("one"))
        );

      Command returned = subject.parseCommand(Collections.singletonList("one"));
      assertThat(returned, Matchers.sameInstance(fromFactory));
    }
  }
}
