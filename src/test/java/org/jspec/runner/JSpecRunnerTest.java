package org.jspec.runner;

import static java.util.Collections.synchronizedList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.*;
import static org.jspec.util.Assertions.assertListEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.hamcrest.Matchers;
import org.jspec.proto.JSpecExamples;
import org.jspec.util.RunListenerSpy.Event;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;

import com.google.common.collect.ImmutableList;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class JSpecRunnerTest {
  public class constructor {
    @Test
    public void givenAContextClassSuitableForJSpecButNotForJUnit_raisesNoError() {
      Runners.of(JSpecExamples.MultiplePublicConstructors.class);
    }

    @Test
    public void givenAConfigurationWith1OrMoreErrors_raisesInitializationErrorWithThoseErrors() {
      TestConfiguration config = configFinding(new IllegalArgumentException(), new AssertionError());
      assertListEquals(Runners.initializationErrorCauses(config).stream().map(Throwable::getClass).collect(toList()),
        ImmutableList.of(IllegalArgumentException.class, AssertionError.class));
    }
  }
  
  public class getDescription {
    public class givenATestConfigurationOrContextClassWith1OrMoreExamples {
      @Test
      public void describesTheConfiguredClass() {
        Description description = Runners.of(JSpecExamples.IgnoredClass.class).getDescription();
        assertThat(description.getTestClass(), equalTo(JSpecExamples.IgnoredClass.class));
        assertThat(description.getAnnotation(Ignore.class), notNullValue());
      }
      
      @Test
      public void hasAChildDescriptionForEachExample() {
        Runner runner = Runners.of(configOf(JSpecExamples.Two.class, exampleNamed("one"), exampleNamed("another")));
        Description subject = runner.getDescription();
        assertListEquals(
          ImmutableList.of("one(org.jspec.proto.JSpecExamples$Two)", "another(org.jspec.proto.JSpecExamples$Two)"), 
          subject.getChildren().stream().map(Description::getDisplayName).collect(toList()));
      }
    }
  }
  
  public class run {
    private final List<Event> events = synchronizedList(new LinkedList<Event>());
    private final Class<?> context = JSpecExamples.One.class;
    
    public class givenAPassingExample {
      @Before
      public void setup() throws Exception {
        Runner runner = Runners.of(configOf(context, exampleSpy("passing", events::add)));
        Runners.runAll(runner, events::add);
      }
      
      @Test
      public void runsBetweenNotifyStartAndFinish() {
        assertListEquals(ImmutableList.of("testStarted", "run::passing", "testFinished"),
          events.stream().map(Event::getName).collect(toList()));
        assertThat(events.stream().map(Event::getDisplayName).collect(toList()), 
          contains(Matchers.startsWith("passing"), anything(), Matchers.startsWith("passing")));
      }
    }
    
    public class givenAFailingExample {
      @Before
      public void setup() throws Exception {
        Runner runner = Runners.of(configOf(context, exampleFailing("boom"), exampleSpy("successor", events::add)));
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
  }
  
  private static TestConfiguration configOf(Class<?> contextClass, Example... examples) {
    return new TestConfiguration() {
      @Override
      public List<Throwable> findInitializationErrors() { return Collections.emptyList(); }
      
      @Override
      public Class<?> getContextClass() { return contextClass; }

      @Override
      public Stream<Example> getExamples() { return Stream.of(examples); }
    };
  }
  
  private static TestConfiguration configFinding(Throwable... errors) {
    TestConfiguration stub = mock(TestConfiguration.class);
    stub(stub.findInitializationErrors()).toReturn(Arrays.asList(errors));
    doThrow(new UnsupportedOperationException("invalid context class")).when(stub).getExamples();
    return stub;
  }

  private static Example exampleFailing(String behaviorName) throws Exception {
    Example stub = exampleNamed(behaviorName);
    doThrow(new AssertionError("bang!")).when(stub).run();
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
      public void run() { notify.accept(Event.named("run::" + behaviorName)); }
    };
  }
}