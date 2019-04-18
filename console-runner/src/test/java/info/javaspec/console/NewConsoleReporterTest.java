package info.javaspec.console;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.Spec;
import info.javaspec.SpecCollection;
import info.javaspec.testutil.Assertions;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.*;

@RunWith(HierarchicalContextRunner.class)
public class NewConsoleReporterTest {
  private Reporter subject;
  private MockPrintStream output;

  @Before
  public void setup() throws Exception {
    output = MockPrintStream.create();
    subject = new NewConsoleReporter(output);
  }

  public class writeMessage {
    @Test
    public void writesTheGivenLinesWithNoIndentation() throws Exception {
      subject.writeMessage(Arrays.asList("one", "two"));
      output.shouldHavePrintedTheseLines(
        equalTo("one"),
        equalTo("two")
      );
    }
  }

  public class whenRunningSpecs {
    public class givenNoSpecs {
      @Test
      public void printsZeroTotals() throws Exception {
        subjectRuns(() -> {});
        output.shouldHavePrintedTheseLines(
          equalTo("[Testing complete] Passed: 0, Failed: 0, Total: 0")
        );
      }
    }

    public class givenOneOrMoreSpecs {
      @Test @Ignore
      public void printsASeparatorBeforeTheFinalResult() throws Exception {
      }
    }
  }

  public class givenASpec {
    @Test
    public void printsTheSpecsAsAListItem() throws Exception {
      subjectRuns(() -> {
        Spec spec = anySpecNamed("behaves");
        subject.specStarting(spec);
        subject.specPassed(spec);
      });

      output.shouldHavePrintedTheseLines(
        startsWith("- behaves"),
        closingMessageMatcher()
      );
    }

    @Test
    public void printsTheBehaviorAndResultForAPassingSpec() throws Exception {
      subjectRuns(() -> {
        Spec spec = anySpecNamed("behaves");
        subject.specStarting(spec);
        subject.specPassed(spec);
      });

      output.shouldHavePrintedTheseLines(
        endsWith("behaves: PASS"),
        closingMessageMatcher()
      );
    }

    @Test
    public void printsTheBehaviorAndResultForAFailingSpec() throws Exception {
      subjectRuns(() -> {
        Spec spec = anySpecNamed("behaves");
        subject.specStarting(spec);
        subject.specFailed(spec);
      });

      output.shouldHavePrintedTheseLines(
        endsWith("behaves: FAIL"),
        closingMessageMatcher()
      );
    }
  }

  /* Setup */

  private SpecCollection anyCollection() {
    SpecCollection collection = Mockito.mock(SpecCollection.class);
    Mockito.when(collection.description()).thenReturn("<default description>");
    return collection;
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

  /* Action helpers */

  private void subjectRuns(Assertions.Thunk thunk) throws Exception {
    subject.runStarting();
    thunk.run();
    subject.runFinished();
  }

  private Matcher<String> closingMessageMatcher() {
    return startsWith("[Testing complete]");
  }
}
