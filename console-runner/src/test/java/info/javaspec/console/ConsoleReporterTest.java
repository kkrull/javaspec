package info.javaspec.console;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;

@RunWith(HierarchicalContextRunner.class)
public class ConsoleReporterTest {
  private Reporter subject;
  private PrintStream output;

  public class writeMessage {
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
}
