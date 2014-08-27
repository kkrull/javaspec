package org.javaspec.runner;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.synchronizedList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.*;
import static org.javaspec.testutil.Assertions.assertListEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.javaspec.proto.ContextClasses;
import org.javaspec.runner.JavaSpecRunner.NoExamplesException;
import org.javaspec.testutil.RunListenerSpy.Event;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;

import com.google.common.collect.ImmutableList;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class JavaSpecRunnerTest {
  public class constructor {
    @Test
    public void givenAContextClassSuitableForJavaSpecButNotForJUnit_raisesNoError() {
      Runners.of(ContextClasses.TwoConstructors.class);
    }
    
    @Test
    public void givenAGatewayWith1OrMoreErrors_raisesInitializationErrorWithThoseErrors() {
      ExampleGateway gateway = gatewayFinding(new IllegalArgumentException(), new AssertionError());
      assertListEquals(Runners.initializationErrorCauses(gateway).map(Throwable::getClass).collect(toList()),
        ImmutableList.of(IllegalArgumentException.class, AssertionError.class));
    }
    
    @Test
    public void givenAGatewayWithNoExamples_raisesInitializationErrorContainingNoExamplesException() {
      ExampleGateway gateway = gatewayWithNoExamples(contextNamed("top-level context"));
      List<Throwable> causes = Runners.initializationErrorCauses(gateway).collect(toList());
      assertThat(causes, contains(instanceOf(NoExamplesException.class)));
      assertThat(causes.stream().map(Throwable::getMessage).collect(toList()), contains(equalTo(
        "Test context 'top-level context' must contain at least 1 example in an It field")));
    }

    private Context contextNamed(String name) {
      return new Context(name, ImmutableList.of());
    }
    
    private ExampleGateway gatewayFinding(Throwable... errors) {
      ExampleGateway stub = mock(ExampleGateway.class);
      when(stub.findInitializationErrors()).thenReturn(Arrays.asList(errors));
      when(stub.hasExamples()).thenReturn(true);
      return stub;
    }

    private ExampleGateway gatewayWithNoExamples(Context root) {
      ExampleGateway gateway = mock(ExampleGateway.class);
      when(gateway.getRootContext()).thenReturn(root);
      when(gateway.getRootContextName()).thenReturn(root.name);
      return gateway;
    }
  }
  
  public class getDescription {
    public class givenAGatewayWith1OrMoreExamples {
      public class andAContextOf1OrMoreLevels {
        private final ExampleGateway gateway = gatewayRepeatingExample(exampleNamed("runs"), "top", "middle", "bottom");
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
        
        private ExampleGateway gatewayRepeatingExample(NewExample example, String top, String middle, String bottom) {
          ExampleGateway gateway = mock(ExampleGateway.class);
          when(gateway.findInitializationErrors()).thenReturn(newArrayList());
          when(gateway.hasExamples()).thenReturn(true);
          
          NewExample[] examples = { example, example, example };
          Context[] contexts = { 
            new Context(top, newArrayList(example.describeBehavior())),
            new Context(middle, newArrayList(example.describeBehavior())),
            new Context(bottom, newArrayList(example.describeBehavior())),
          };
          
          Stream<NewExample> exampleStream = Stream.of(examples);
          when(gateway.getExamples()).thenReturn(exampleStream);
          
          when(gateway.getRootContext()).thenReturn(contexts[0]);
          when(gateway.getRootContextName()).thenReturn(top);
          when(gateway.getSubContexts(contexts[0])).thenReturn(newArrayList(contexts[1]));
          when(gateway.getSubContexts(contexts[1])).thenReturn(newArrayList(contexts[2]));
          when(gateway.getSubContexts(contexts[2])).thenReturn(newArrayList());
          
          return gateway;
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
    
    public class givenASkippedExample {
      private final NewExample skipped = exampleSkipped();
      
      @Before
      public void setup() throws Exception {
        Runner runner = Runners.of(gatewayWithExamples(skipped));
        Runners.runAll(runner, events::add);
      }

      @Test
      public void doesNotRunTheExample() throws Exception {
        verify(skipped, never()).run();
      }
      
      @Test
      public void notifiesTestIgnored() {
        assertThat(events.stream().map(Event::getName).collect(toList()), contains(equalTo("testIgnored")));
      }
    }
    
    public class givenAPassingExample {
      @Before
      public void setup() throws Exception {
        Runner runner = Runners.of(gatewayWithExamples(exampleSpy("passing", events::add)));
        Runners.runAll(runner, events::add);
      }
      
      @Test
      public void runsBetweenNotifyStartAndFinish() {
        assertListEquals(ImmutableList.of("testStarted", "run::passing", "testFinished"),
          events.stream().map(Event::getName).collect(toList()));
        assertThat(events.stream().map(Event::describedMethodName).collect(toList()),
          contains(equalTo("passing"), anything(), equalTo("passing")));
      }
    }
    
    public class givenAFailingExample {
      @Before
      public void setup() throws Exception {
        Runner runner = Runners.of(gatewayWithExamples(exampleFailing("boom"), exampleSpy("successor", events::add)));
        Runners.runAll(runner, events::add);
      }
      
      @Test
      public void notifiesTestFailed() {
        assertThat(events.stream().map(Event::getName).collect(toList()), hasItem(equalTo("testFailure")));
        assertThat(events.stream().map(x -> x.failure).collect(toList()), hasItem(notNullValue()));
      }
      
      @Test
      public void continuesRunningSuccessiveTests() {
        assertThat(events.stream().map(Event::getName).collect(toList()), contains(
          "testStarted", "testFailure", "testFinished",
          "testStarted", "run::successor", "testFinished"));
      }
    }

    private NewExample exampleFailing(String behaviorName) throws Exception {
      NewExample stub = exampleNamed(behaviorName);
      doThrow(new AssertionError("bang!")).when(stub).run();
      return stub;
    }
    
    private NewExample exampleSkipped() {
      NewExample stub = exampleNamed("skipper");
      when(stub.isSkipped()).thenReturn(true);
      return stub;
    }
    
    private NewExample exampleSpy(String behaviorName, Consumer<Event> notify) throws Exception {
      NewExample stub = mock(NewExample.class);
      when(stub.describeBehavior()).thenReturn(behaviorName);
      doAnswer(invocation -> {
        notify.accept(Event.named("run::" + behaviorName));
        return null;
      }).when(stub).run();
      return stub;
    }
    
    private ExampleGateway gatewayWithExamples(NewExample... examples) {
      ExampleGateway stub = mock(ExampleGateway.class);
      
      List<String> exampleNames = Stream.of(examples).map(NewExample::describeBehavior).collect(toList());
      when(stub.getRootContext()).thenReturn(new Context("root", exampleNames));
      when(stub.getRootContextName()).thenReturn("root");
      when(stub.hasExamples()).thenReturn(examples.length > 0);
      
      Stream<NewExample> streamOfExamples = Stream.of(examples);
      when(stub.getExamples()).thenReturn(streamOfExamples);
      return stub;
    }
  }
  
  private NewExample exampleNamed(String behaviorName) {
    NewExample stub = mock(NewExample.class);
    when(stub.describeBehavior()).thenReturn(behaviorName);
    return stub;
  }
}