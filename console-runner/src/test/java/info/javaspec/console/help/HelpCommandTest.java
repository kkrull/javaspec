package info.javaspec.console.help;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.console.Command;
import info.javaspec.console.help.HelpCommand;
import info.javaspec.console.help.HelpObserver;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

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
    public void writesGeneralForm() throws Exception {
      subject.run();
      observer.writeMessageShouldHaveReceivedLine("Usage: javaspec <command> [<arguments>]");
    }

    @Test
    public void listsEachKnownCommand() throws Exception {
      subject.run();
      observer.writeMessageShouldHaveReceivedCommand("help", "show a list of commands, or help on a specific command");
      observer.writeMessageShouldHaveReceivedCommand("run", "run specs in Java classes");
    }
  }

  private static final class MockHelpObserver implements HelpObserver {
    private final List<String> writeMessageReceived = new LinkedList<>();

    @Override
    public void writeMessage(List<String> lines) {
      this.writeMessageReceived.addAll(lines);
    }

    public void writeMessageShouldHaveReceivedCommand(String command, String description) {
      Optional<String> matchingLine = this.writeMessageReceived.stream()
        .filter(line -> line.startsWith(command))
        .findFirst();

      assertThat(matchingLine.isPresent(), is(true));
      assertThat(matchingLine.get(), endsWith(description));
    }

    public void writeMessageShouldHaveReceivedLine(String line) {
      assertThat(this.writeMessageReceived, hasItem(equalTo(line)));
    }
  }
}
