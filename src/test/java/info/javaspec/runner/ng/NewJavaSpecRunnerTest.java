package info.javaspec.runner.ng;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.runner.ng.NewJavaSpecRunner.NoExamplesException;
import info.javaspecproto.ContextClasses;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.mockito.Mockito;

import java.util.stream.Stream;

import static info.javaspec.testutil.Assertions.capture;
import static info.javaspec.testutil.Matchers.matchesRegex;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
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
        givenGatewayWithNoExamples("ContextClasses$Empty");
        Exception ex = capture(NoExamplesException.class, () -> new NewJavaSpecRunner(gateway));
        assertThat(ex.getMessage(), matchesRegex("^Context .*[$]Empty must contain at least 1 example"));
      }
    }
  }

  public class getDescription {
    @Test
    public void returnsTheDescriptionProvidedByTheGateway() throws Exception {
      givenGatewayReturning(Description.EMPTY);
      subject = new NewJavaSpecRunner(gateway);
      assertThat(subject.getDescription(), sameInstance(Description.EMPTY));
    }
  }

  public class testCount {
    public class givenAClassWith1OrMoreExamples {
      @Test
      public void returnsTheNumberOfTestsInTheGivenContextClass() throws Exception {
        givenGatewayWithExamples("first_test", "second_test");
        subject = new NewJavaSpecRunner(gateway);
        assertThat(subject.testCount(), equalTo(2));
      }
    }
  }

  private void givenGatewayReturning(Description description) {
    when(gateway.rootContextName()).thenReturn("SingleExample");
    when(gateway.hasExamples()).thenReturn(true);
    when(gateway.totalNumExamples()).thenReturn(1);
    when(gateway.junitDescriptionTree()).thenReturn(description);
  }

  private void givenGatewayWithNoExamples(String rootContextName) {
    when(gateway.rootContextName()).thenReturn(rootContextName);
    when(gateway.hasExamples()).thenReturn(false);
    when(gateway.totalNumExamples()).thenReturn(0);
    when(gateway.junitDescriptionTree()).thenReturn(Description.EMPTY);
  }

  private void givenGatewayWithExamples(String... exampleNames) {
    when(gateway.rootContextName()).thenReturn("ContextWithExamples");
    when(gateway.hasExamples()).thenReturn(true);
    when(gateway.totalNumExamples()).thenReturn(exampleNames.length);

    Description suite = Description.createSuiteDescription("ContextWithExamples");
    Stream.of(exampleNames)
      .map(x -> Description.createTestDescription("ContextWithExamples", x))
      .forEach(suite::addChild);
    when(gateway.junitDescriptionTree()).thenReturn(suite);
  }
}