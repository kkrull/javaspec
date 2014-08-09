package org.jspec.runner;

import static java.util.Collections.synchronizedList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.*;
import static org.jspec.util.Assertions.assertListEquals;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.jspec.proto.JSpecExamples;
import org.jspec.util.RunListenerSpy.Event;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runners.model.InitializationError;

import com.google.common.collect.ImmutableList;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class JSpecRunnerTest {
  public class constructor {
    @Test
    public void givenAConfigurationWithoutErrors_raisesNoError() {
      Runners.of(configOf(JSpecExamples.One.class));
    }

    @Test
    public void givenAConfigurationWith1OrMoreErrors_raisesInitializationErrorWithThoseErrors() {
      TestConfiguration config = configFinding(new IllegalArgumentException(), new AssertionError());
      assertInitializationError(config, ImmutableList.of(IllegalArgumentException.class, AssertionError.class));
    }
    
    @Test
    public void givenAContextClassSuitableForJSpecButNotForJUnit_raisesNoError() {
      Runners.of(JSpecExamples.MultiplePublicConstructors.class);
    }

    private void assertInitializationError(TestConfiguration config, List<Class<? extends Throwable>> expectedCauses) {
      try {
        new JSpecRunner(config);
      } catch (InitializationError ex) {
        assertListEquals(expectedCauses, Runners.flattenCauses(ex).map(Throwable::getClass).collect(toList()));
        return;
      }
      fail(String.format("Expected causes of initialization error to be <%s>, but nothing was thrown", expectedCauses));
    }
  }
  
  public class getDescription {
    public class givenATestConfigurationOrContextClass {
      @Test
      public void describesTheConfiguredClass() {
        assertDescribesIgnoredClass(Runners.of(JSpecExamples.IgnoredClass.class));
        assertDescribesIgnoredClass(Runners.of(configOf(JSpecExamples.IgnoredClass.class)));
      }
      
      private void assertDescribesIgnoredClass(Runner runner) {
        Description description = runner.getDescription();
        assertThat(description.getTestClass(), equalTo(JSpecExamples.IgnoredClass.class));
        assertThat(description.getAnnotation(Ignore.class), notNullValue());
      }
    }
  
    public class givenATestConfigurationWith1OrMoreExamples {
      private final Description subject;
      
      public givenATestConfigurationWith1OrMoreExamples() throws Exception {
        Runner runner = Runners.of(configOf(JSpecExamples.Two.class, exampleNamed("one"), exampleNamed("another")));
        this.subject = runner.getDescription();
      }
      
      @Test
      public void hasAChildDescriptionForEachExample() {
        ArrayList<Description> children = subject.getChildren();
        assertListEquals(
          ImmutableList.of("one(org.jspec.proto.JSpecExamples$Two)", "another(org.jspec.proto.JSpecExamples$Two)"), 
          children.stream().map(Description::getDisplayName).collect(toList()));
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
        assertThat(events.stream().map(Event::getName).collect(toList()), 
          contains(is("testStarted"), is("run::passing"), is("testFinished")));
        assertThat(events.stream().map(Event::getDisplayName).collect(toList()), 
          contains(startsWith("passing"), anything(), startsWith("passing")));
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
        assertThat(events.stream().map(Event::getName).collect(toList()), hasItem(is("testFailure")));
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
      public boolean hasInitializationErrors() { return false; }
      
      @Override
      public Class<?> getContextClass() { return contextClass; }

      @Override
      public Stream<Example> getExamples() { return Stream.of(examples); }
    };
  }
  
  private static TestConfiguration configFinding(Throwable... errors) {
    return new TestConfiguration() {
      @Override
      public List<Throwable> findInitializationErrors() { return Arrays.asList(errors); }

      @Override
      public boolean hasInitializationErrors() { return errors.length > 0; }

      @Override
      public Class<?> getContextClass() { return JSpecExamples.One.class; }

      @Override
      public Stream<Example> getExamples() { 
        String msg = String.format("This configuration is invalid, finding %s", findInitializationErrors());
        throw new IllegalStateException(msg);
      }
    };
  }
  
  private static Example exampleNamed(String behaviorName) {
    return new Example() {
      @Override
      public String describeBehavior() { return behaviorName; }
      
      @Override
      public void run() { return; }
    };
  }

  private static Example exampleFailing(String behaviorName) {
    return new Example() {
      @Override
      public String describeBehavior() { return behaviorName; }
      
      @Override
      public void run() { assertEquals(1, 2); }
    };
  }
    
  private static Example exampleSpy(String behaviorName, Consumer<Event> notify) {
    return new Example() {
      @Override
      public String describeBehavior() { return behaviorName; }

      @Override
      public void run() { notify.accept(Event.named("run::" + behaviorName)); }
    };
  }
}