package info.javaspec.console.help;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.internal.Console;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.console.Command;
import info.javaspec.console.Result;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(HierarchicalContextRunner.class)
public class HelpCommandTest {
  private HelpCommand subject;
  private MockHelpObserver observer;

  public class run {
    @Before
    public void setup() throws Exception {
      observer = new MockHelpObserver();
      subject = new HelpCommand(observer);
    }

    @Test
    public void returns0() throws Exception {
      Result result = subject.run();
      assertThat(result.exitCode, equalTo(0));
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

  public class runWithJCommander {
    @Test
    public void printsUsageFromJCommander() throws Exception {
      observer = new MockHelpObserver();
      JCommander jCommander = JCommander.newBuilder()
        .programName("javaspec")
        .build();
      subject = new HelpCommand(observer);

      subject.run(jCommander);
      observer.writeMessageShouldHaveReceivedLine("Usage: javaspec\n");
    }

    @Test
    public void restoresJCommanderToItsOriginalState() throws Exception {
      Console originalConsole = Mockito.mock(Console.class);
      JCommander jCommander = JCommander.newBuilder()
        .console(originalConsole)
        .build();

      subject = new HelpCommand(Mockito.mock(HelpObserver.class));
      subject.run(jCommander);
      assertThat(jCommander.getConsole(), sameInstance(originalConsole));
    }

    @Test
    public void returnsASuccessfulResult() throws Exception {
      observer = new MockHelpObserver();
      JCommander jCommander = JCommander.newBuilder().build();
      subject = new HelpCommand(observer);

      Result result = subject.run(jCommander);
      assertThat(result.exitCode, equalTo(0));
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
