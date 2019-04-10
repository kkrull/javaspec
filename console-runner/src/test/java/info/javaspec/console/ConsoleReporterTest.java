package info.javaspec.console;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.Spec;
import info.javaspec.SpecCollection;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(HierarchicalContextRunner.class)
public class ConsoleReporterTest {
  private Reporter subject;
  private MockPrintStream output;

  @Before
  public void setup() throws Exception {
    output = MockPrintStream.create();
    subject = new ConsoleReporter(output);
  }

  public class beginCollection {
    @Test
    public void printsTheDescriptionForTheFirstSpecCollection() throws Exception {
      subject.beginCollection(anyCollectionDescribing("first"));
      output.shouldHavePrintedLine(equalTo("first"));
    }

    @Test
    public void printsANewlineBetweenSpecCollections() throws Exception {
      subject.beginCollection(anyCollectionDescribing("first"));
      subject.beginCollection(anyCollectionDescribing("second"));
      output.shouldHavePrintedTheseLines(
        equalTo("first"),
        isEmptyString(),
        equalTo("second")
      );
    }
  }

  public class runFinished {
    @Test
    public void printsANewlineFollowedBySpecCounts() throws Exception {
      subject.runFinished();
      output.shouldHavePrintedLine(isEmptyString());
      output.shouldHavePrintedLine(equalTo("[Testing complete] Passed: 0, Failed: 0, Total: 0"));
    }
  }

  public class specFailed {
    @Test
    public void printsFailAndNewline() throws Exception {
      Spec spec = anySpecNamed("should work");
      subject.specStarting(spec);
      subject.specFailed(spec);
      output.shouldHavePrintedLine(endsWith("should work: FAIL"));
    }
  }

  public class specPassed {
    @Test
    public void printsPassAndNewline() throws Exception {
      Spec spec = anySpecNamed("works");
      subject.specStarting(spec);
      subject.specPassed(spec);
      output.shouldHavePrintedLine(endsWith("works: PASS"));
    }
  }

  public class specStarting {
    public class whenTheSpecIsInATopLevelSubjectCollection {
      @Test
      public void printsTheSpecBehaviorAsAListItemWithoutAnyIndentation() throws Exception {
        subject.specStarting(anySpecNamed("does its thing"));
        output.shouldHavePrintedLine(equalTo("- does its thing"));
      }
    }

    public class whenTheSpecIsInANestedSubjectCollection {
      @Test @Ignore
      public void printsTheSpecBehaviorAsAListItemWithIndentation() throws Exception {
        subject.beginCollection(anyCollectionDescribing("some specific circumstance"));
        subject.specStarting(anySpecNamed("does something specific"));
        output.shouldHavePrintedLine(equalTo("  - does something specific"));
      }
    }
  }

  public class writeMessage {
    @Test
    public void writesNothingGivenAnEmptyList() throws Exception {
      subject.writeMessage(Collections.emptyList());
      output.outputShouldBe(Matchers.isEmptyString());
    }

    @Test
    public void writesOneLineForEachGivenString() throws Exception {
      subject.writeMessage(Arrays.asList("one", "two"));
      output.shouldHavePrintedLine(equalTo("one"));
      output.shouldHavePrintedLine(equalTo("two"));
    }
  }

  private SpecCollection anyCollectionDescribing(String description) {
    SpecCollection collection = Mockito.mock(SpecCollection.class);
    Mockito.when(collection.description()).thenReturn(description);
    return collection;
  }

  private Spec anySpecNamed(String behavior) {
    Spec spec = Mockito.mock(Spec.class);
    Mockito.when(spec.intendedBehavior()).thenReturn(behavior);
    return spec;
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

    public void outputShouldBe(Matcher<String> matcher) {
      assertThat(this.printedBytes.toString(), matcher);
    }

    public void shouldHavePrintedLine(Matcher<String> matcher) {
      assertThat(printedLines(), hasItem(matcher));
    }

    @SafeVarargs
    public final void shouldHavePrintedTheseLines(Matcher<String>... lineMatchers) {
      assertThat(printedLines(), Matchers.contains(lineMatchers));
    }

    private List<String> printedLines() {
      String concatenatedOutput = this.printedBytes.toString();
      String[] lines = concatenatedOutput.split(System.lineSeparator());
      return Arrays.asList(lines);
    }
  }
}
