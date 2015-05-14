package info.javaspec.runner.ng;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.runner.ng.NewJavaSpecRunner.NoExamplesException;
import info.javaspecproto.ContextClasses;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;

import static info.javaspec.testutil.Assertions.capture;
import static info.javaspec.testutil.Matchers.matchesRegex;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.fail;
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
    private Description description;

    public class givenAClassWith1Example {
      @Before
      public void setup() {
        givenGatewayWithExamples(ContextClasses.OneIt.class, "only_test");
        subject = new NewJavaSpecRunner(gateway);
        description = subject.getDescription();
      }

      @Test
      public void returnsATestDescriptionForTheGivenClass() throws Exception {
        assertThat(description.isSuite(), equalTo(false));
        assertThat(description.isTest(), equalTo(true));
        assertThat(description.getClassName(), matchesRegex("^.*ContextClasses[$]OneIt$"));
      }

      @Test
      public void namesTheTestWithTheNameOfTheExample() throws Exception {
        assertThat(description.getMethodName(), equalTo("only_test"));
      }
    }

    public class givenAClassWith2OrMoreExamples {
      @Before
      public void setup() {
        givenGatewayWithExamples(ContextClasses.TwoIt.class, "first_test", "second_test");
        subject = new NewJavaSpecRunner(gateway);
        description = subject.getDescription();
      }

      @Test
      public void returnsASuiteDescriptionForTheGivenClass() {
        assertThat(description.isSuite(), equalTo(true));
        assertThat(description.isTest(), equalTo(false));
        assertThat(description.getClassName(), matchesRegex("^.*ContextClasses[$]TwoIt$"));
      }

      @Test
      public void containsChildTestDescriptionsForEachExampleInTheGivenClass() {
        assertThat(description.getChildren(), hasSize(2));
        assertThat(description.getChildren().stream().map(Description::getClassName).collect(toList()),
          contains(matchesRegex("^.*ContextClasses[$]TwoIt$"), matchesRegex("^.*ContextClasses[$]TwoIt$")));
        assertThat(description.getChildren().stream().map(Description::getMethodName).collect(toList()),
          contains("first_test", "second_test"));
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
    when(gateway.exampleNames()).thenReturn(Arrays.asList(its));
  }

  private void givenGatewayWithNoExamples(Class<?> context) {
    Mockito.<Class<?>>when(gateway.getContextClass()).thenReturn(context);
    when(gateway.hasExamples()).thenReturn(false);
    when(gateway.numExamples()).thenReturn(0);
    when(gateway.exampleNames()).thenReturn(new ArrayList<>(0));
  }
}