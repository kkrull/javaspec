package org.javaspec.runner;

import static java.util.Collections.synchronizedList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.*;
import static org.javaspec.testutil.Assertions.assertListEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.hamcrest.Matchers;
import org.javaspec.proto.ContextClasses;
import org.javaspec.testutil.RunListenerSpy.Event;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.mockito.Mockito;

import com.google.common.collect.ImmutableList;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class JavaSpecRunnerTest {
  public class constructor {
    @Test @Ignore("wip")
    public void givenAContextClassSuitableForJavaSpecButNotForJUnit_raisesNoError() {
      Runners.of(ContextClasses.TwoConstructors.class);
    }

    @Test
    public void givenAGatewayWith1OrMoreErrors_raisesInitializationErrorWithThoseErrors() {
      ExampleGateway gateway = gatewayFinding(new IllegalArgumentException(), new AssertionError());
      assertListEquals(Runners.initializationErrorCauses(gateway).stream().map(Throwable::getClass).collect(toList()),
        ImmutableList.of(IllegalArgumentException.class, AssertionError.class));
    }
  }
  
  public class getDescription {
    public class givenAGatewayWith1OrMoreExamples {
      public class andAContextOf1OrMoreLevels {
        private final ExampleGateway gateway = gatewayWithRepeatedExample("runs", contextOf("top", "middle", "bottom"));
        private final Description returned = Runners.of(gateway).getDescription();
        
        @Test
        public void describesEachContextAsASuite() {
          assertThat(returned.getClassName(), equalTo("top"));
          assertThat(childClassNames(returned), contains(equalTo("middle")));
          Description middleSuite = childSuites(returned).findFirst().get();
          assertThat(childClassNames(middleSuite), contains(equalTo("bottom")));
        }

        @Test
        public void describesEachExampleAsATestInAContextClass() {
          assertHasTest(returned, "top", "runs");
          
          Description middleSuite = childSuites(returned).findFirst().get();
          assertHasTest(middleSuite, "middle", "runs");
          
          Description bottomSuite = childSuites(middleSuite).findFirst().get();
          assertHasTest(bottomSuite, "bottom", "runs");
        }

        private void assertHasTest(Description suite, String contextName, String exampleName) {
          List<Description> tests = suite.getChildren().stream().filter(Description::isTest).collect(toList());
          assertThat(tests.stream().map(Description::getClassName).collect(toList()), contains(equalTo(contextName)));
          assertThat(tests.stream().map(Description::getMethodName).collect(toList()), contains(equalTo(exampleName)));
        }

        private List<String> childClassNames(Description suite) {
          return childSuites(suite).map(Description::getClassName).collect(toList());
        }

        private Stream<Description> childSuites(Description suite) {
          return suite.getChildren().stream().filter(Description::isSuite);
        }
      }
    }
  }
  
  public class run {
    private final List<Event> events = synchronizedList(new LinkedList<Event>());
    private final Class<?> context = ContextClasses.OneIt.class;
    
    public class givenASkippedExample {
      private final Example skipped = exampleSkipped();
      
      @Before
      public void setup() throws Exception {
        Runner runner = Runners.of(gatewayFor(context.getName(), skipped));
        Runners.runAll(runner, events::add);
      }

      @Test @Ignore("wip")
      public void doesNotRunTheExample() throws Exception {
        verify(skipped, never()).run();
      }
      
      @Test @Ignore("wip")
      public void notifiesTestIgnored() {
        assertThat(events.stream().map(Event::getName).collect(toList()),
          contains(equalTo("testIgnored")));
      }
    }
    
    public class givenAPassingExample {
      @Before
      public void setup() throws Exception {
        Runner runner = Runners.of(gatewayFor(context.getName(), exampleSpy("passing", events::add)));
        Runners.runAll(runner, events::add);
      }
      
      @Test @Ignore("wip")
      public void runsBetweenNotifyStartAndFinish() {
        assertListEquals(ImmutableList.of("testStarted", "run::passing", "testFinished"),
          events.stream().map(Event::getName).collect(toList()));
        assertThat(events.stream().map(Event::describedDisplayName).collect(toList()), 
          contains(Matchers.startsWith("passing"), anything(), Matchers.startsWith("passing")));
      }
    }
    
    public class givenAFailingExample {
      @Before
      public void setup() throws Exception {
        Runner runner = Runners.of(gatewayFor(context.getName(), exampleFailing("boom"), exampleSpy("successor", events::add)));
        Runners.runAll(runner, events::add);
      }
      
      @Test @Ignore("wip")
      public void notifiesTestFailed() {
        assertThat(events.stream().map(Event::getName).collect(toList()), hasItem(equalTo("testFailure")));
        assertThat(events.stream().map(x -> x.failure).collect(toList()), hasItem(notNullValue()));
      }
      
      @Test @Ignore("wip")
      public void continuesRunningSuccessiveTests() {
        assertThat(events.stream().map(Event::getName).collect(toList()), contains(
          "testStarted", "testFailure", "testFinished",
          "testStarted", "run::successor", "testFinished"));
      }
    }
  }
  
  private Context contextOf(String topName, String middleName, String bottomName) {
    return new Context(topName, ImmutableList.of(
      new Context(middleName, ImmutableList.of(
        new Context(bottomName)))));
  }
  
  private static ExampleGateway gatewayFinding(Throwable... errors) {
    ExampleGateway stub = mock(ExampleGateway.class);
    stub(stub.findInitializationErrors()).toReturn(Arrays.asList(errors));
    doThrow(new UnsupportedOperationException("invalid context class")).when(stub).getExampleNames(any());
    return stub;
  }

  private static ExampleGateway gatewayFor(String contextName, Example... examples) {
    return new ExampleGateway() {
      @Override
      public List<Throwable> findInitializationErrors() { return Collections.emptyList(); }
      
      @Override
      public Context getContextRoot() { return new Context(contextName); }
      
      @Override
      public List<String> getExampleNames(Context context) {
        return Stream.of(examples).map(Example::describeBehavior).collect(toList()); 
      }
    };
  }

  private static ExampleGateway gatewayWithRepeatedExample(String exampleName, Context root) {
    ExampleGateway gateway = Mockito.mock(ExampleGateway.class);
    when(gateway.getContextRoot()).thenReturn(root);
    when(gateway.getExampleNames(Mockito.any())).thenReturn(ImmutableList.of(exampleName));
    return gateway;
  }

  private static Example exampleFailing(String behaviorName) throws Exception {
    Example stub = exampleNamed(behaviorName);
    doThrow(new AssertionError("bang!")).when(stub).run();
    return stub;
  }
  
  private static Example exampleSkipped() {
    Example stub = exampleNamed("skipper");
    stub(stub.isSkipped()).toReturn(true);
    return stub;
  }
  
  private static Example exampleNamed(String behaviorName) {
    Example stub = mock(Example.class);
    stub(stub.describeBehavior()).toReturn(behaviorName);
    return stub;
  }
  
  private static Example exampleSpy(String behaviorName, Consumer<Event> notify) {
    return new Example() {
      @Override
      public String describeSetup() { return ""; }
      
      @Override
      public String describeAction() { return ""; }
      
      @Override
      public String describeBehavior() { return behaviorName; }

      @Override
      public String describeCleanup() { return ""; }
      
      @Override
      public boolean isSkipped() { return false; }
      
      @Override
      public void run() { notify.accept(Event.named("run::" + behaviorName)); }
    };
  }
}