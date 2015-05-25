package info.javaspec.runner.ng;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.runner.ng.NewJavaSpecRunner.NoExamples;
import info.javaspec.testutil.RunListenerSpy;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;

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
import static org.mockito.Mockito.when;

@RunWith(HierarchicalContextRunner.class)
public class NewJavaSpecRunnerTest {
  private final NewExampleGateway gateway = mock(NewExampleGateway.class);
  private final Runner subject;

  public NewJavaSpecRunnerTest() {
    when(gateway.hasExamples()).thenReturn(true);
    subject = new NewJavaSpecRunner(gateway);
  }

  public class constructor {
    public class givenAClassWithoutAnyExamples {
      @Test
      public void throwsNoExamplesException() throws Exception {
        givenTheGatewayHasNoExamples("ContextClasses$Empty");
        Exception ex = capture(NoExamples.class, () -> new NewJavaSpecRunner(gateway));
        assertThat(ex.getMessage(), matchesRegex("^Context ContextClasses[$]Empty must contain at least 1 example"));
      }
    }
  }

  public class getDescription {
    @Test
    public void returnsTheDescriptionProvidedByTheGateway() throws Exception {
      givenTheGatewayDescribes(1, Description.EMPTY);
      assertThat(subject.getDescription(), sameInstance(Description.EMPTY));
    }
  }

  public class run {
    final List<RunListenerSpy.Event> events = synchronizedList(new LinkedList<>());
    private final RunListenerSpy listener = new RunListenerSpy(events::add);
    private final RunNotifier notifier = new RunNotifier();

    @Before
    public void setup() throws Exception {
      notifier.addListener(listener);
    }

    public class givenAnIgnoredExample {
      @Before
      public void setup() throws Exception {
        givenTheGatewayDescribes(1, aSuiteWithATest("IgnoreMe"));
        subject.run(notifier);
      }

      @Test
      public void notifiesOfTheTestRunStarting() throws Exception {
        List<String> methodNames = events.stream().map(RunListenerSpy.Event::getName).collect(toList());
        assertThat(methodNames, contains("testIgnored"));
      }
    }

    public class givenAContextClassWith1OrMoreExamples {
      @Before
      public void setup() throws Exception {
        givenTheGatewayDescribes(4L, aSuiteWith("ContextWithNestedExamples",
            Description.createTestDescription("ContextWithNestedExamples", "root context example 1"),
            Description.createTestDescription("ContextWithNestedExamples", "root context example 2"),
            aSuiteWithATest("subcontext 1"),
            aSuiteWithATest("subcontext 2"))
        );

        subject.run(notifier);
      }

      @Test
      public void notifiesOfEachExamplesOutcome() throws Exception {
        List<String> methodNames = events.stream().map(RunListenerSpy.Event::describedMethodName).collect(toList());
        assertThat(methodNames, contains(
          "root context example 1", "root context example 2", "subcontext 1", "subcontext 2"));
      }
    }
  }

  public class testCount {
    public class givenAClassWith1OrMoreExamples {
      @Test
      public void returnsTheNumberOfTestsInTheGivenContextClass() throws Exception {
        givenTheGatewayDescribes(2, aSuiteWith("TwoLeafExamples",
          Description.createTestDescription("TwoLeafExamples", "1"),
          Description.createTestDescription("TwoLeafExamples", "2")));
        assertThat(subject.testCount(), equalTo(2));
      }
    }

    public class givenAClassWithMoreExamplesThanThereAreIntegers {
      @Test
      public void throwsTooManyTests() throws Exception {
        givenTheGatewayHasAnEnormousNumberOfExamples("ContextWithLotsOfExamples");
        NewJavaSpecRunner.TooManyExamples ex = capture(NewJavaSpecRunner.TooManyExamples.class,
          () -> new NewJavaSpecRunner(gateway).testCount());
        assertThat(ex.getMessage(),
          equalTo("Context ContextWithLotsOfExamples has more examples than JUnit can support in a single class: 2147483648"));
      }
    }
  }

  private void givenTheGatewayHasAnEnormousNumberOfExamples(String contextName) {
    when(gateway.rootContextName()).thenReturn(contextName);
    when(gateway.totalNumExamples()).thenReturn((long)Integer.MAX_VALUE + 1);
  }

  private void givenTheGatewayHasNoExamples(String rootContextName) {
    when(gateway.rootContextName()).thenReturn(rootContextName);
    when(gateway.hasExamples()).thenReturn(false);
  }

  private void givenTheGatewayDescribes(long numTests, Description description) {
    when(gateway.rootContextName()).thenReturn("RootContext");
    when(gateway.totalNumExamples()).thenReturn(numTests);
    when(gateway.junitDescriptionTree()).thenReturn(description);
  }

  private static Description aSuiteWith(String name, Description... children) {
    Description suite = Description.createSuiteDescription(name);
    Stream.of(children).forEach(suite::addChild);
    return suite;
  }

  private static Description aSuiteWithATest(String name) {
    Description suite = Description.createSuiteDescription(name);
    suite.addChild(Description.createTestDescription(name, name));
    return suite;
  }
}