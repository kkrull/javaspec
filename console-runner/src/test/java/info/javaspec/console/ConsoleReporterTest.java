package info.javaspec.console;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;

@RunWith(HierarchicalContextRunner.class)
public class ConsoleReporterTest {
  private Reporter subject;

  public class runFinished {
    private MockPrintStream output;

    @Before
    public void setup() throws Exception {
      output = MockPrintStream.create();
    }

    @Test
    public void printsASummaryOfPassedFailedAndTotal() throws Exception {
      subject = new ConsoleReporter(output);
      subject.runFinished();
      output.printlnShouldHaveReceivedLine(equalTo("[Testing complete] Passed: 0, Failed: 0, Total: 0"));
    }
  }

  public class writeMessage {
    private PrintStream output;

    @Test
    public void writesNothingGivenAnEmptyList() throws Exception {
      output = Mockito.mock(PrintStream.class);
      subject = new ConsoleReporter(output);

      subject.writeMessage(Collections.emptyList());
      Mockito.verifyNoMoreInteractions(output);
    }

    @Test
    public void writesOneLineForEachGivenString() throws Exception {
      output = Mockito.mock(PrintStream.class);
      subject = new ConsoleReporter(output);

      subject.writeMessage(Arrays.asList("one", "two"));
      Mockito.verify(output).println("one");
      Mockito.verify(output).println("two");
      Mockito.verifyNoMoreInteractions(output);
    }
  }

  static final class MockPrintStream extends PrintStream {
    private final ByteArrayOutputStream printedBytes;

    public static MockPrintStream create() {
      return new MockPrintStream(new ByteArrayOutputStream());
    }

    public MockPrintStream(ByteArrayOutputStream printedBytes) {
      super(printedBytes);
      this.printedBytes = printedBytes;
    }

    public void printlnShouldHaveReceivedLine(Matcher<String> matcher) {
      assertThat(printedLines(), hasItem(matcher));
    }

    private List<String> printedLines() {
      String concatenatedOutput = this.printedBytes.toString();
      String[] lines = concatenatedOutput.split(System.lineSeparator());
      return Arrays.asList(lines);
    }
  }
}
