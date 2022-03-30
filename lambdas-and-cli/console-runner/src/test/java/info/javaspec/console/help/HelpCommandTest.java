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
    @Test
    public void printsUsageFromJCommander() throws Exception {
      observer = new MockHelpObserver();
      JCommander jCommander = JCommander.newBuilder()
        .programName("javaspec")
        .build();
      subject = new HelpCommand(observer, jCommander);

      subject.run();
      observer.writeMessageShouldHaveReceivedLine("Usage: javaspec\n");
    }

    @Test
    public void restoresJCommanderToItsOriginalState() throws Exception {
      Console originalConsole = Mockito.mock(Console.class);
      JCommander jCommander = JCommander.newBuilder()
        .console(originalConsole)
        .build();

      subject = new HelpCommand(Mockito.mock(HelpObserver.class), jCommander);
      subject.run();
      assertThat(jCommander.getConsole(), sameInstance(originalConsole));
    }

    @Test
    public void returnsASuccessfulResult() throws Exception {
      observer = new MockHelpObserver();
      JCommander jCommander = anyJCommander();
      subject = new HelpCommand(observer, jCommander);

      Result result = subject.run();
      assertThat(result.exitCode, equalTo(0));
    }
  }

  private JCommander anyJCommander() {
    return JCommander.newBuilder().build();
  }

  private static final class MockHelpObserver implements HelpObserver {
    private final List<String> writeMessageReceived = new LinkedList<>();

    @Override
    public void writeMessage(List<String> lines) {
      this.writeMessageReceived.addAll(lines);
    }

    public void writeMessageShouldHaveReceivedLine(String line) {
      assertThat(this.writeMessageReceived, hasItem(equalTo(line)));
    }
  }
}
