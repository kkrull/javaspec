package info.javaspec.runner.ng;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.runner.ng.NewJavaSpecRunner.NoExamplesException;
import info.javaspecproto.ContextClasses;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.mockito.Mockito;

import static info.javaspec.testutil.Assertions.capture;
import static info.javaspec.testutil.Matchers.matchesRegex;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(HierarchicalContextRunner.class)
public class NewJavaSpecRunnerTest {
  private final NewExampleGateway gateway = mock(NewExampleGateway.class);
  private Runner subject;

  public class constructor {
    public class givenAClassWithoutAnyExamples {
      @Test
      public void throwsNoExamplesException() throws Exception {
        givenGatewayWithNoExamples(ContextClasses.Empty.class);
        Exception ex = capture(NoExamplesException.class, () -> new NewJavaSpecRunner(gateway));
        assertThat(ex.getMessage(),
          matchesRegex("^Context class .*[$]Empty must contain at least 1 example in an It field$"));
      }
    }
  }

  public class getDescription {
    public class givenAClassWith1Example {
      @Test
      public void returnsAnAtomicTestDescription() throws Exception {
        givenGatewayWithExamples(ContextClasses.OneIt.class, "only_test");
        subject = new NewJavaSpecRunner(gateway);
        subject.getDescription();
      }
    }
  }

  public class testCount {
    public class givenAClassWith1OrMoreExamples {
      @Test
      public void returnsTheNumberOfTestsInTheGivenContextClass() throws Exception {
        givenGatewayWithExamples(ContextClasses.TwoIt.class, "first_test", "second_test");
        subject = new NewJavaSpecRunner(gateway);
        assertThat(subject.testCount(), equalTo(2));
      }
    }
  }

  private void givenGatewayWithExamples(Class<?> context, String... its) {
    Mockito.<Class<?>>when(gateway.getContextClass()).thenReturn(context);
    when(gateway.hasExamples()).thenReturn(true);
    when(gateway.numExamples()).thenReturn(its.length);
  }

  private void givenGatewayWithNoExamples(Class<?> context) {
    Mockito.<Class<?>>when(gateway.getContextClass()).thenReturn(context);
    when(gateway.hasExamples()).thenReturn(false);
    when(gateway.numExamples()).thenReturn(0);
  }
}