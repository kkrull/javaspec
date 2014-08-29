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

import org.javaspec.runner.JavaSpecRunner.NoExamplesException;
import org.javaspec.testutil.RunListenerSpy.Event;
import org.javaspecproto.ContextClasses;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.ImmutableList;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class JavaSpecRunnerTest {
  private @Mock ExampleGateway gateway;
  private @Mock Example example;
  
  @Before
  public void initMocks() { MockitoAnnotations.initMocks(this); }
  
  public class constructor {
    @Test
    public void givenAContextClassSuitableForJavaSpecButNotForJUnit_raisesNoError() {
      Runners.of(ContextClasses.TwoConstructors.class);
    }
    
    @Test
    public void givenAGatewayWith1OrMoreErrors_raisesInitializationErrorWithThoseErrors() {
      gatewayFinds(new IllegalArgumentException(), new AssertionError());
      assertListEquals(Runners.initializationErrorCauses(gateway).map(Throwable::getClass).collect(toList()),
        ImmutableList.of(IllegalArgumentException.class, AssertionError.class));
    }
    
    @Test
    public void givenAGatewayWithNoExamples_raisesInitializationErrorContainingNoExamplesException() {
      gatewayHasRootContext(contextNamed("top-level context"));
      List<Throwable> causes = Runners.initializationErrorCauses(gateway).collect(toList());
      assertThat(causes, contains(instanceOf(NoExamplesException.class)));
      assertThat(causes.stream().map(Throwable::getMessage).collect(toList()), contains(equalTo(
        "Test context 'top-level context' must contain at least 1 example in an It field")));
    }
  }
  
  public class getDescription {
    public class givenAGatewayWith1OrMoreExamples {
      public class andAContextOf1OrMoreLevels {
        private Description returned;
        
        @Before
        public void setup() {
          exampleHas("runs");
          gatewayRepeatingExample(example, "top", "middleWithNoTests", "bottom");
          returned = Runners.of(gateway).getDescription();
        }
        
        @Test
        public void describesEachContextAsASuite() {
          assertThat(returned.getClassName(), equalTo("top"));
          assertThat(childClassNames(returned), contains(equalTo("middleWithNoTests")));
          Description middleSuite = childSuites(returned).findFirst().get();
          assertThat(childClassNames(middleSuite), contains(equalTo("bottom")));
        }

        @Test
        public void describesEachExampleAsATestInAContextClass() {
          assertHasTest(returned, "top", "runs");
          
          Description middleSuite = childSuites(returned).findFirst().get();
          assertHasTest(middleSuite, "middleWithNoTests", "runs");
          
          Description bottomSuite = childSuites(middleSuite).findFirst().get();
          assertHasTest(bottomSuite, "bottom", "runs");
        }
        
        private void gatewayRepeatingExample(Example example, String top, String middle, String bottom) {
          gatewayHasExamples(example, example, example);
          Context[] contexts = { 
            new Context(1, top, newArrayList(example.getName())),
            new Context(2, middle, newArrayList(example.getName())),
            new Context(3, bottom, newArrayList(example.getName())),
          };
          
          gatewayHasRootContext(contexts[0]);
          when(gateway.getSubContexts(contexts[0])).thenReturn(newArrayList(contexts[1]));
          when(gateway.getSubContexts(contexts[1])).thenReturn(newArrayList(contexts[2]));
          when(gateway.getSubContexts(contexts[2])).thenReturn(newArrayList());
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
      @Before
      public void setup() throws Exception {
        exampleIsSkipped();
        gatewayHasTopLevelExamples(example);
        Runners.runAll(Runners.of(gateway), events::add);
      }

      @Test
      public void doesNotRunTheExample() throws Exception {
        verify(example, never()).run();
      }
      
      @Test
      public void notifiesTestIgnored() {
        assertThat(events.stream().map(Event::getName).collect(toList()), contains(equalTo("testIgnored")));
      }
    }
    
    public class givenAPassingExample {
      @Before
      public void setup() throws Exception {
        exampleSpies("passing", events::add);
        gatewayHasTopLevelExamples(example);
        Runners.runAll(Runners.of(gateway), events::add);
      }
      
      @Test @SuppressWarnings("unchecked")
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
        exampleSpies("successor", events::add);
        gatewayHasTopLevelExamples(failingExample("boom"), example);
        Runners.runAll(Runners.of(gateway), events::add);
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
    
    private Example failingExample(String behaviorName) throws Exception {
      Example stub = mock(Example.class);
      when(stub.getName()).thenReturn(behaviorName);
      doThrow(new AssertionError("bang!")).when(stub).run();
      return stub;
    }
  }
  
  private Context contextNamed(String name) {
    return new Context(1, name, ImmutableList.of());
  }
  
  private void exampleHas(String behaviorName) {
    when(example.getName()).thenReturn(behaviorName);
  }
  
  private void exampleIsSkipped() {
    when(example.isSkipped()).thenReturn(true);
  }
  
  private void exampleSpies(String behaviorName, Consumer<Event> notify) throws Exception {
    when(example.getName()).thenReturn(behaviorName);
    doAnswer(invocation -> {
      notify.accept(Event.named("run::" + behaviorName));
      return null;
    }).when(example).run();
  }
  
  private void gatewayFinds(Throwable... errors) {
    when(gateway.findInitializationErrors()).thenReturn(Arrays.asList(errors));
    when(gateway.hasExamples()).thenReturn(true);
  }
  
  private void gatewayHasTopLevelExamples(Example... examples) {
    List<String> exampleNames = Stream.of(examples).map(Example::getName).collect(toList());
    gatewayHasRootContext(new Context(1, "root", exampleNames));
    gatewayHasExamples(examples);
  }
  
  private void gatewayHasExamples(Example... examples) {
    Stream<Example> exampleStream = Stream.of(examples);
    when(gateway.getExamples()).thenReturn(exampleStream);
    when(gateway.hasExamples()).thenReturn(true);
  }
  
  private void gatewayHasRootContext(Context root) {
    when(gateway.getRootContext()).thenReturn(root);
    when(gateway.getRootContextName()).thenReturn(root.name);
  }
}