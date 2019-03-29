package info.javaspec.console;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@RunWith(HierarchicalContextRunner.class)
public class InvalidCommandTest {
  public class noCommandNamed {
    @Test
    public void reportsTheInvalidCommand() throws Exception {
      Exception command = ArgumentParser.InvalidCommand.noCommandNamed("oracle");
      assertThat(command.getMessage(), equalTo("Invalid command: oracle"));
    }
  }
}
