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

  public class beginCollection {
    @Test
    public void printsTheDescription() throws Exception {
      subjectRuns(() -> {
        SpecCollection collection = anyCollectionDescribing("widgets");
        subject.beginCollection(collection);
        subject.endCollection(collection);
      });

      output.shouldHavePrintedLine(containsString("widgets"));
    }

    public class givenAnInnerCollection {
      @Test
      public void increasesIndentation() throws Exception {
        subjectRuns(() -> {
          SpecCollection outer = anyCollectionDescribing("outer");
          subject.beginCollection(outer);

          SpecCollection inner = anyCollectionDescribing("inner");
          subject.beginCollection(inner);
          subject.endCollection(inner);

          subject.endCollection(outer);
        });

        output.shouldHavePrintedLine(startsWith("  inner"));
      }

      @Test
      public void continuesTheSameParagraph() throws Exception {
        subjectRuns(() -> {
          SpecCollection outer = anyCollectionDescribing("outer");
          subject.beginCollection(outer);

          SpecCollection inner = anyCollectionDescribing("inner");
          subject.beginCollection(inner);
          subject.endCollection(inner);

          subject.endCollection(outer);
        });

        output.shouldHavePrintedExactly(
          startsWith("outer"),
          containsString("inner"),
          isEmptyString(),
          testTallyMatcher()
        );
      }
    }

    public class givenTwoOrMoreCollectionsWithTheSameParentCollection {
      @Test
      public void indentsThoseCollectionsTheSame() throws Exception {
        subjectRuns(() -> {
          SpecCollection first = anyCollectionDescribing("first");
          subject.beginCollection(first);
          subject.endCollection(first);

          SpecCollection second = anyCollectionDescribing("second");
          subject.beginCollection(second);
          subject.endCollection(second);
        });

        output.shouldHavePrintedLines(
          startsWith("first"),
          startsWith("second")
        );
      }

      @Test
      public void startsANewParagraphForEachCollectionAfterTheFirstOne() throws Exception {
        subjectRuns(() -> {
          SpecCollection first = anyCollectionDescribing("first");
          subject.beginCollection(first);
          subject.endCollection(first);

          SpecCollection second = anyCollectionDescribing("second");
          subject.beginCollection(second);
          subject.endCollection(second);
        });

        output.shouldHavePrintedExactly(
          startsWith("first"),
          isEmptyString(),
          startsWith("second"),
          isEmptyString(),
          testTallyMatcher()
        );
      }
    }
  }

  public class specFailed {
    @Test
    public void indicatesWhichSpecFailed() throws Exception {
      subjectRuns(() -> {
        Spec spec = anySpecNamed("behaves");
        subject.specStarting(spec);
        subject.specFailed(spec);
      });

      output.shouldHavePrintedLine(endsWith("behaves: FAIL"));
    }
  }

  public class specPassed {
    @Test
    public void indicatesWhichSpecPassed() throws Exception {
      subjectRuns(() -> {
        Spec spec = anySpecNamed("behaves");
        subject.specStarting(spec);
        subject.specPassed(spec);
      });

      output.shouldHavePrintedLine(endsWith("behaves: PASS"));
    }
  }

  public class specStarting {
    @Test
    public void printsTheSpecAsAListItem() throws Exception {
      subjectRuns(() -> {
        Spec spec = anySpecNamed("behaves");
        subject.specStarting(spec);
        subject.specPassed(spec);
      });

      output.shouldHavePrintedLine(containsString("- behaves"));
    }

    public class whenCollectionContainingTheSpecIsIndented {
      @Test
      public void indentsSpecsFlushWithThatCollection() throws Exception {
        subjectRuns(() -> {
          SpecCollection outer = anyCollection();
          subject.beginCollection(outer);

          SpecCollection inner = anyCollectionDescribing("inner");
          subject.beginCollection(inner);

          Spec spec = anySpecNamed("spec");
          subject.specStarting(spec);
          subject.specPassed(spec);

          subject.endCollection(inner);
          subject.endCollection(outer);
        });

        output.shouldHavePrintedLines(
          startsWith("  inner"),
          startsWith("  - spec")
        );
      }
    }

    public class afterAnEarlierCollectionHasEnded {
      @Test
      public void decreasesIndentationToMatchTheCollectionThatIsStillInScope() throws Exception {
        subjectRuns(() -> {
          SpecCollection outer = anyCollectionDescribing("outer");
          subject.beginCollection(outer);

          SpecCollection inner = anyCollectionDescribing("inner");
          subject.beginCollection(inner);
          subject.endCollection(inner);

          Spec spec = anySpecNamed("outer spec");
          subject.specStarting(spec);
          subject.specPassed(spec);

          subject.endCollection(outer);
        });

        output.shouldHavePrintedLine(startsWith("- outer spec"));
      }
    }

    public class givenTwoOuterCollectionsWithSpecs {
      @Test
      public void indentsThoseSpecsTheSame() throws Exception {
        subjectRuns(() -> {
          SpecCollection firstCollection = anyCollectionDescribing("first");
          subject.beginCollection(firstCollection);
          subject.endCollection(firstCollection);

          SpecCollection secondCollection = anyCollectionDescribing("second");
          subject.beginCollection(secondCollection);

          Spec spec = anySpecNamed("second spec");
          subject.specStarting(spec);
          subject.specPassed(spec);

          subject.endCollection(secondCollection);
        });

        output.shouldHavePrintedLine(startsWith("- second spec"));
      }
    }
  }

  public class runFinished {
    public class givenNoSpecs {
      @Test
      public void printsZeroTotals() throws Exception {
        subjectRuns(() -> {});
        output.shouldHavePrintedLine(equalTo("[Testing complete] Passed: 0, Failed: 0, Total: 0"));
      }
    }

    public class givenNoSpecsOrCollections {
      @Test
      public void doesNotStartANewParagraph() throws Exception {
        subjectRuns(() -> {});
        output.shouldHavePrintedExactly(
          equalTo("[Testing complete] Passed: 0, Failed: 0, Total: 0")
        );
      }
    }

    public class givenOneOrMoreCollections {
      @Test
      public void startsANewParagraph() throws Exception {
        subjectRuns(() -> {
          SpecCollection collection = anyCollectionDescribing("widgets");
          subject.beginCollection(collection);
          subject.endCollection(collection);
        });

        output.shouldHavePrintedExactly(
          containsString("widgets"),
          isEmptyString(),
          testTallyMatcher()
        );
      }
    }

    public class givenOneOrMoreSpecs {
      @Test @Ignore
      public void addsThoseToTheTotalNumberOfSpecs() throws Exception {
      }
    }

    public class givenOneOrMoreFailingSpecs {
      @Test @Ignore
      public void addsThoseToTheTotalNumberOfFailingSpecs() throws Exception {
      }

      @Test
      public void startsANewParagraph() throws Exception {
        subjectRuns(() -> {
          Spec spec = anySpecNamed("behaves");
          subject.specStarting(spec);
          subject.specFailed(spec);
        });

        output.shouldHavePrintedExactly(
          containsString("behaves"),
          isEmptyString(),
          testTallyMatcher()
        );
      }
    }

    public class givenOneOrMorePassingSpecs {
      @Test @Ignore
      public void addsThoseToTheTotalNumberOfPassingSpecs() throws Exception {
      }

      @Test
      public void startsANewParagraph() throws Exception {
        subjectRuns(() -> {
          Spec spec = anySpecNamed("behaves");
          subject.specStarting(spec);
          subject.specPassed(spec);
        });

        output.shouldHavePrintedExactly(
          containsString("behaves"),
          isEmptyString(),
          testTallyMatcher()
        );
      }
    }
  }

  public class writeMessage {
    @Test
    public void printsThoseLinesWithoutIndentation() throws Exception {
      subject.writeMessage(Arrays.asList("one", "two"));
      output.shouldHavePrintedExactly(
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

  private Matcher<String> testTallyMatcher() {
    return startsWith("[Testing complete]");
  }
}
