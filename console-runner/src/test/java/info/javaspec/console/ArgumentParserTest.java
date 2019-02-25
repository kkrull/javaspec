package info.javaspec.console;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;

@RunWith(HierarchicalContextRunner.class)
public class ArgumentParserTest {
  private Main.CommandParser subject;

  public class parseCommand {
    @Test
    public void createsRunSpecsCommandWithAllArgsAsClassNames() throws Exception {
      subject = new ArgumentParser(null);
      Command command = subject.parseCommand("");
      assertThat(command, instanceOf(RunSpecsCommand.class));
    }
  }
}
