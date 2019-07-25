package info.javaspec.console.help;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.console.Command;
import info.javaspec.console.help.DetailedHelpCommand;
import info.javaspec.console.help.HelpObserver;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(HierarchicalContextRunner.class)
public class DetailedHelpCommandTest {
  private Command subject;
  private MockHelpObserver observer;

  public class run {
    @Before
    public void setup() throws Exception {
      observer = new MockHelpObserver();
      subject = new DetailedHelpCommand(observer, "run");
    }

    @Test
    public void returns0() throws Exception {
      int status = subject.run();
      assertThat(status, equalTo(0));
    }

    @Test
    public void writestheGeneralForm() throws Exception {
      subject.run();
      observer.writeMessageShouldHaveReceivedLine(startsWith("Usage:   javaspec run"));
    }

    @Test
    public void writesAnExample() throws Exception {
      subject.run();
      observer.writeMessageShouldHaveReceivedLine(startsWith("Example: javaspec run"));
    }

    @Test
    public void listsEachOption() throws Exception {
      subject.run();
      observer.writeMessageShouldHaveReceivedLine(startsWith("--reporter=[reporter]"));
      observer.writeMessageShouldHaveReceivedLine(containsString("plaintext"));
    }
  }

  private static final class MockHelpObserver implements HelpObserver {
    private final List<String> writeMessageReceived = new LinkedList<>();

    @Override
    public void writeMessage(List<String> lines) {
      this.writeMessageReceived.addAll(lines);
    }

    public void writeMessageShouldHaveReceivedLine(Matcher<String> lineMatcher) {
      assertThat(this.writeMessageReceived, hasItem(lineMatcher));
    }
  }
}
