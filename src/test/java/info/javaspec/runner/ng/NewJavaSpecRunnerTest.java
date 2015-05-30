package info.javaspec.runner.ng;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.runner.ng.NewJavaSpecRunner.NoSpecs;
import info.javaspec.runner.ng.NewJavaSpecRunner.TooManySpecs;
import info.javaspec.testutil.RunListenerSpy;
import org.junit.Before;
import org.junit.Ignore;
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
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(HierarchicalContextRunner.class)
public class NewJavaSpecRunnerTest {
  private final SpecGateway gateway = mock(SpecGateway.class);
  private final Runner subject;

  public NewJavaSpecRunnerTest() {
    when(gateway.hasSpecs()).thenReturn(true);
    subject = new NewJavaSpecRunner(gateway);
  }

  public class constructor {
    @Test
    public void givenAClassWithoutAnySpecs_throwsNoSpecs() throws Exception {
      givenTheGatewayHasNoSpecs("ContextClasses$Empty");
      Exception ex = capture(NoSpecs.class, () -> new NewJavaSpecRunner(gateway));
      assertThat(ex.getMessage(), matchesRegex("^Context ContextClasses[$]Empty must contain at least 1 spec"));
    }
  }

  public class getDescription {
    @Ignore
    @Test
    public void returnsTheDescriptionProvidedByTheGateway() throws Exception {
      givenTheGatewayDescribes(1, Description.EMPTY);
//      assertThat(subject.getDescription(), sameInstance(Description.EMPTY));
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

    public class givenAnIgnoredSpec {
      @Before
      public void setup() throws Exception {
        givenTheGatewayDescribes(1, aSuiteWithATest("IgnoreMe"));
//        subject.run(notifier);
      }

      @Ignore
      @Test
      public void notifiesOfTheTestRunStarting() throws Exception {
        List<String> methodNames = events.stream().map(RunListenerSpy.Event::getName).collect(toList());
        assertThat(methodNames, contains("testIgnored"));
      }
    }

    public class givenAContextClassWith1OrMoreSpecs {
      @Before
      public void setup() throws Exception {
        givenTheGatewayDescribes(4L, aSuiteWith("ContextWithNestedSpecs",
            Description.createTestDescription("ContextWithNestedSpecs", "root context Spec 1"),
            Description.createTestDescription("ContextWithNestedSpecs", "root context Spec 2"),
            aSuiteWithATest("subcontext 1"),
            aSuiteWithATest("subcontext 2"))
        );

//        subject.run(notifier);
      }

      @Test
      @Ignore
      public void notifiesOfEachSpecsOutcome() throws Exception {
        List<String> methodNames = events.stream().map(RunListenerSpy.Event::describedMethodName).collect(toList());
        assertThat(methodNames, contains(
          "root context Spec 1", "root context Spec 2", "subcontext 1", "subcontext 2"));
      }
    }
  }

  public class testCount {
    public class givenAClassWith1OrMoreSpecs {
      @Test
      public void returnsTheNumberOfTestsInTheGivenContextClass() throws Exception {
        givenTheGatewayHasSpecs(2, aContext("Root", aSpec("one"), aSpec("two")));
        assertThat(subject.testCount(), equalTo(2));
      }
    }

    public class givenAClassWithMoreSpecsThanThereAreIntegers {
      @Test
      public void throwsTooManyTests() throws Exception {
        givenTheGatewayHasAnEnormousNumberOfSpecs("BigContext");
        TooManySpecs ex = capture(TooManySpecs.class, () -> new NewJavaSpecRunner(gateway).testCount());
        assertThat(ex.getMessage(), equalTo("Context BigContext has more specs than JUnit can support: 2147483648"));
      }
    }
  }

  private void givenTheGatewayHasAnEnormousNumberOfSpecs(String rootContextId) {
    when(gateway.rootContextId()).thenReturn(rootContextId);
    when(gateway.hasSpecs()).thenReturn(true);
    when(gateway.countSpecs()).thenReturn((long) (Integer.MAX_VALUE) + 1);
  }

  private void givenTheGatewayHasNoSpecs(String rootContextId) {
    when(gateway.rootContextId()).thenReturn(rootContextId);
    when(gateway.hasSpecs()).thenReturn(false);
  }

  private void givenTheGatewayHasSpecs(long numSpecs, Context rootContext) {
    when(gateway.rootContextId()).thenReturn(rootContext.id);
    when(gateway.hasSpecs()).thenReturn(true);
    when(gateway.countSpecs()).thenReturn(numSpecs);
  }

  private Context aContext(String id, Spec... specs) {
    return new Context(id) { };
  }

  private Spec aSpec(String id) {
    return new Spec() { };
  }

  private void givenTheGatewayDescribes(long numTests, Description description) {
//    when(gateway.rootContextName()).thenReturn("RootContext");
//    when(gateway.totalNumSpecs()).thenReturn(numTests);
//    when(gateway.junitDescriptionTree()).thenReturn(description);
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