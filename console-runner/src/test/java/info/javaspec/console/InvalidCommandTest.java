package info.javaspec.console;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.console.ArgumentParser.InvalidCommand;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@RunWith(HierarchicalContextRunner.class)
public class InvalidCommandTest {
  public class noCommandNamed {
    @Test
    public void reportsTheInvalidCommand() throws Exception {
      Exception command = InvalidCommand.noCommandNamed("oracle");
      assertThat(command.getMessage(), equalTo("Invalid command: oracle"));
    }
  }

  public class noReporterDefied {
    @Test
    public void reportsTheInvalidCommand() throws Exception {
      Exception command = InvalidCommand.noReporterDefined(Arrays.asList("run com.megacorp.widget"));
      assertThat(command.getMessage(), equalTo("No reporter specified.  Please use the --reporter option"));
    }
  }
}
