package info.javaspec.runner.ng;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.runner.ng.NewJavaSpecRunner.NoExamples;
import info.javaspec.testutil.RunListenerSpy;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.mockito.Mockito;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import static info.javaspec.testutil.Assertions.capture;
import static info.javaspec.testutil.Matchers.matchesRegex;
import static java.util.Collections.synchronizedList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
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
        Exception ex = capture(NoExamples.class, () -> new NewJavaSpecRunner(gateway));
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

  public class run {
    final List<RunListenerSpy.Event> events = synchronizedList(new LinkedList<>());
    private final RunListenerSpy listener = new RunListenerSpy(events::add);
    private final RunNotifier notifier = new RunNotifier();

    @Before
    public void setup() throws Exception {
      Mockito.stub(gateway.hasExamples()).toReturn(true);
      subject = new NewJavaSpecRunner(gateway);
      notifier.addListener(listener);
    }

    public class givenAnIgnoredExample {
      @Before
      public void setup() throws Exception {
        givenGatewayWithExamples("should pass");
        subject.run(notifier);
      }

      @Test @Ignore
      public void notifiesOfTheTestRunStarting() throws Exception {
        List<String> methodNames = events.stream().map(RunListenerSpy.Event::getName).collect(toList());
        assertThat(methodNames, contains("testIgnored"));
      }
    }

    public class givenAContextClassWith1OrMoreExamples {
      @Before
      public void setup() throws Exception {
        when(gateway.rootContextName()).thenReturn("ContextWithNestedExamples");
        when(gateway.totalNumExamples()).thenReturn(4L);

        Description rootSuite = Description.createSuiteDescription("ContextWithNestedExamples");
        rootSuite.addChild(Description.createTestDescription("ContextWithNestedExamples", "root context example 1"));
        rootSuite.addChild(Description.createTestDescription("ContextWithNestedExamples", "root context example 2"));
        rootSuite.addChild(suiteWithATest("subcontext 1"));
        rootSuite.addChild(suiteWithATest("subcontext 2"));
        when(gateway.junitDescriptionTree()).thenReturn(rootSuite);

        subject.run(notifier);
      }

      @Test
      public void notifiesOfEachExamplesOutcome() throws Exception {
        List<String> methodNames = events.stream().map(RunListenerSpy.Event::describedMethodName).collect(toList());
        assertThat(methodNames, contains(
          "root context example 1",
          "root context example 2",
          "subcontext 1",
          "subcontext 2"
        ));
      }
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

    public class givenAClassWithMoreExamplesThanThereAreInts {
      @Test
      public void throwsTooManyTests() throws Exception {
        givenGatewayWithAnEnormousNumberOfExamples("ContextWithLotsOfExamples");
        NewJavaSpecRunner.TooManyExamples ex = capture(NewJavaSpecRunner.TooManyExamples.class,
          () -> new NewJavaSpecRunner(gateway).testCount());
        assertThat(ex.getMessage(),
          equalTo("Context ContextWithLotsOfExamples has more examples than JUnit can support in a single class: 2147483648"));
      }
    }
  }

  private void givenGatewayReturning(Description description) {
    when(gateway.rootContextName()).thenReturn("SingleExample");
    when(gateway.hasExamples()).thenReturn(true);
    when(gateway.totalNumExamples()).thenReturn(1L);
    when(gateway.junitDescriptionTree()).thenReturn(description);
  }

  private void givenGatewayWithNoExamples(String rootContextName) {
    when(gateway.rootContextName()).thenReturn(rootContextName);
    when(gateway.hasExamples()).thenReturn(false);
    when(gateway.totalNumExamples()).thenReturn(0L);
    when(gateway.junitDescriptionTree()).thenReturn(Description.EMPTY);
  }

  private void givenGatewayWithExamples(String... exampleNames) {
    when(gateway.rootContextName()).thenReturn("ContextWithExamples");
    when(gateway.hasExamples()).thenReturn(true);
    when(gateway.totalNumExamples()).thenReturn((long)exampleNames.length);

    Description suite = Description.createSuiteDescription("ContextWithExamples");
    Stream.of(exampleNames)
      .map(x -> Description.createTestDescription("ContextWithExamples", x))
      .forEach(suite::addChild);
    when(gateway.junitDescriptionTree()).thenReturn(suite);
  }

  private void givenGatewayWithAnEnormousNumberOfExamples(String contextName) {
    when(gateway.rootContextName()).thenReturn(contextName);
    when(gateway.hasExamples()).thenReturn(true);
    when(gateway.totalNumExamples()).thenReturn((long)Integer.MAX_VALUE + 1);
    when(gateway.junitDescriptionTree()).thenThrow(UnsupportedOperationException.class);
  }

  private Description suiteWithATest(String name) {
    Description suite = Description.createSuiteDescription(name);
    suite.addChild(Description.createTestDescription(name, name));
    return suite;
  }
}