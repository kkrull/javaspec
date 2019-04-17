package info.javaspec.console;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.Spec;
import info.javaspec.SpecCollection;
import info.javaspec.testutil.Assertions;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;

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
    public class whenThereAreNoOtherCollectionsInScope {
      @Test
      public void printsTheDescriptionForTheFirstSpecCollection() throws Exception {
        subjectRuns(() -> subject.beginCollection(anyCollectionDescribing("first")));
        output.shouldHavePrintedTheseLines(
          equalTo("first"),
          isEmptyString(),
          closingMessageMatcher()
        );
      }

      @Test
      public void printsANewlineBetweenSpecCollections() throws Exception {
        subjectRuns(() -> {
          SpecCollection first = anyCollectionDescribing("first");
          subject.beginCollection(first);
          subject.endCollection(first);

          SpecCollection second = anyCollectionDescribing("second");
          subject.beginCollection(second);
          subject.endCollection(second);
        });

        output.shouldHavePrintedTheseLines(
          equalTo("first"),
          isEmptyString(),
          equalTo("second"),
          isEmptyString(),
          closingMessageMatcher()
        );
      }
    }

    public class whenThereAreOtherCollectionsStillInScope {
      @Test
      public void indentsTheNewCollection() throws Exception {
        subjectRuns(() -> {
          SpecCollection outer = anyCollectionDescribing("outer");
          subject.beginCollection(outer);

          SpecCollection inner = anyCollectionDescribing("inner");
          subject.beginCollection(inner);
          subject.endCollection(inner);

          subject.endCollection(outer);
        });

        output.shouldHavePrintedTheseLines(
          equalTo("outer"),
          equalTo("  inner"),
          isEmptyString(),
          closingMessageMatcher()
        );
      }
    }
  }

  public class runFinished {
    @Test
    public void printsANewlineFollowedBySpecCounts() throws Exception {
      subject.runStarting();
      subject.runFinished();
      output.shouldHavePrintedTheseLines(
        isEmptyString(),
        equalTo("[Testing complete] Passed: 0, Failed: 0, Total: 0")
      );
    }
  }

  public class specFailed {
    @Test
    public void printsFailAndNewline() throws Exception {
      subjectRuns(() -> {
        Spec spec = anySpecNamed("should work");
        subject.specStarting(spec);
        subject.specFailed(spec);
      });

      output.shouldHavePrintedTheseLines(
        endsWith("should work: FAIL"),
        isEmptyString(),
        closingMessageMatcher()
      );
    }
  }

  public class specPassed {
    @Test
    public void printsPassAndNewline() throws Exception {
      subjectRuns(() -> {
        Spec spec = anySpecNamed("works");
        subject.specStarting(spec);
        subject.specPassed(spec);
      });

      output.shouldHavePrintedTheseLines(
        endsWith("works: PASS"),
        isEmptyString(),
        closingMessageMatcher()
      );
    }
  }

  public class specStarting {
    public class whenTheSpecIsInATopLevelSubjectCollection {
      @Test
      public void printsTheSpecBehaviorAsAListItemWithoutAnyIndentation() throws Exception {
        subjectRuns(() -> {
          SpecCollection collection = anyCollection();
          subject.beginCollection(collection);
          subject.specStarting(anySpecNamed("does its thing"));
          subject.endCollection(collection);
        });

        output.shouldHavePrintedTheseLines(
          any(String.class),
          equalTo("- does its thing"),
//          isEmptyString(),
          closingMessageMatcher()
        );
      }
    }

    public class whenTheSpecIsInANestedSubjectCollection {
      @Test
      public void printsTheSpecBehaviorAsAListItemWithIndentation() throws Exception {
        subjectRuns(() -> {
          SpecCollection outer = anyCollectionDescribing("widgets");
          subject.beginCollection(outer);

          SpecCollection inner = anyCollectionDescribing("under some specific circumstance");
          subject.beginCollection(inner);
          subject.specStarting(anySpecNamed("do something specific"));
          subject.endCollection(inner);

          subject.endCollection(outer);
        });

        output.shouldHavePrintedTheseLines(
          equalTo("widgets"),
          equalTo("  under some specific circumstance"),
          equalTo("  - do something specific"),
//          isEmptyString(),
          closingMessageMatcher()
        );
      }
    }
  }

  public class writeMessage {
    @Test
    public void writesNothingGivenAnEmptyList() throws Exception {
      subject.writeMessage(Collections.emptyList());
      output.outputShouldBe(isEmptyString());
    }

    @Test
    public void writesOneLineForEachGivenString() throws Exception {
      subject.writeMessage(Arrays.asList("one", "two"));
      output.shouldHavePrintedTheseLines(
        equalTo("one"),
        equalTo("two")
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
