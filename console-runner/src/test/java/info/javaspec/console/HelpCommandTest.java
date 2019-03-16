package info.javaspec.console;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.console.HelpCommand.HelpObserver;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@RunWith(HierarchicalContextRunner.class)
public class HelpCommandTest {
  private Command subject;
  private MockHelpObserver observer;

  public class run {
    @Before
    public void setup() throws Exception {
      observer = new MockHelpObserver();
      subject = new HelpCommand(observer);
    }

    @Test
    public void returns0() throws Exception {
      int status = subject.run();
      assertThat(status, equalTo(0));
    }

    @Test
    public void writesUsageWithTheGivenObserver() throws Exception {
      subject.run();
      observer.writeMessageShouldHaveReceived(
        "Usage: javaspec <command> [<arguments>]",
        "",
        "Commands:",
        "  help",
        "    show this help",
        "",
        "  run <spec class name> [spec class name...]",
        "    run specs in Java classes"
      );
    }
  }

  private static final class MockHelpObserver implements HelpObserver {
    private final List<String> writeMessageReceived = new LinkedList<>();

    @Override
    public void writeMessage(List<String> lines) {
      this.writeMessageReceived.addAll(lines);
    }

    public void writeMessageShouldHaveReceived(String... expectedLines) {
      List<String> expectedList = Arrays.stream(expectedLines)
        .collect(Collectors.toList());

      assertThat(this.writeMessageReceived, equalTo(expectedList));
    }
  }
}
