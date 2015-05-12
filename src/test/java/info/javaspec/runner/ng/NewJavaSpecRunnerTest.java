package info.javaspec.runner.ng;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.runner.ng.NewJavaSpecRunner.NoExamplesException;
import info.javaspecproto.ContextClasses;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;

import static info.javaspec.testutil.Assertions.capture;
import static info.javaspec.testutil.Matchers.matchesRegex;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@RunWith(HierarchicalContextRunner.class)
public class NewJavaSpecRunnerTest {
  public class constructor {
    public class givenAClassWithoutAnyExamples {
      @Test
      public void throwsNoExamplesException() throws Exception {
        Exception ex = capture(NoExamplesException.class, () -> new NewJavaSpecRunner(ContextClasses.Empty.class));
        assertThat(ex.getMessage(),
          matchesRegex("^Context class .*[$]Empty must contain at least 1 example in an It field$"));
      }
    }
  }

  public class testCount {
    public class givenAClassWith1OrMoreExamples {
      @Test
      public void returnsTheNumberOfTestsInTheGivenContextClass() throws Exception {
        Runner subject = new NewJavaSpecRunner(ContextClasses.OneIt.class);
        assertThat(subject.testCount(), equalTo(1));
      }
    }
  }
}