package org.jspec.runner;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.emptyList;
import static java.util.Collections.synchronizedList;
import static org.hamcrest.Matchers.*;
import static org.jspec.util.Assertions.assertThrows;
import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jspec.proto.JSpecExamples;
import org.jspec.runner.JSpecRunner;
import org.jspec.runner.JSpecRunner.InvalidConstructorError;
import org.jspec.runner.JSpecRunner.NoExamplesError;
import org.jspec.util.RunListenerSpy;
import org.jspec.util.RunListenerSpy.Event;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class JSpecRunnerTest {
  public class constructor {
    @Test
    public void givenAClassWhoseOnlyPublicConstructorIsNoArg_doesNotInvokeTheConstructor() {
      //Allow the test to run and fail instead of bringing down the whole suite
      runnerFor(JSpecExamples.FaultyConstructor.class);
    }

    @Test
    public void givenAClassWithSomeOtherSetOfConstructors_raisesInitializationError() {
        assertInitializationError(JSpecExamples.HiddenConstructor.class, InvalidConstructorError.class);
        assertInitializationError(JSpecExamples.PublicConstructorWithArgs.class, InvalidConstructorError.class);
        assertThrows(IllegalArgumentException.class, () -> runnerFor(JSpecExamples.MultiplePublicConstructors.class));
    }

    @Test
    public void givenAClassWithNoItFields_raisesNoExamplesError() {
      assertInitializationError(JSpecExamples.Empty.class, NoExamplesError.class);
    }
    
    private void assertInitializationError(Class<?> context, Class<? extends InitializationError> expected) {
      try {
        new JSpecRunner(context);
      } catch (InitializationError ex) {
        Stream<Class<?>> initializationErrors = unearthInitializationErrors(ex).map(Object::getClass);
        assertTrue(initializationErrors.anyMatch(x -> x == expected));
        return;
      }
      fail(String.format("Expected initialization error of type %s, but none was thrown", expected));
    }
  }
  
  public class getDescription {
    @Test
    public void givenAClass_hasTheGivenTestClass() {
      assertEquals(JSpecExamples.Two.class, descriptionOf(JSpecExamples.Two.class).getTestClass());
    }

    @Test
    public void givenAClassWithoutAnnotations_hasEmptyAnnotations() {
      assertEquals(emptyList(), descriptionOf(JSpecExamples.Two.class).getAnnotations());
    }

    @Test
    public void givenAClassWithAnnotations_hasThoseAnnotations() {
      assertNotNull(descriptionOf(JSpecExamples.IgnoredClass.class).getAnnotation(Ignore.class));
    }

    public class givenAClassWith1OrMoreItFields {
      private final Description description = descriptionOf(JSpecExamples.Two.class);

      @Test
      public void hasAChildForEach() {
        List<Description> children = description.getChildren();
        List<String> names = children.stream().map(Description::getDisplayName).sorted().collect(Collectors.toList());
        assertEquals(newArrayList(
          "first_test(org.jspec.proto.JSpecExamples$Two)",
          "second_test(org.jspec.proto.JSpecExamples$Two)"),
          names);
      }

      @Test
      public void hasTestCountForEachField() {
        assertEquals(2, description.testCount());
      }
    }

    private Description descriptionOf(Class<?> testClass) {
      JSpecRunner runner = runnerFor(testClass);
      return runner.getDescription();
    }
  }

  public class run {
    private final List<Event> events = synchronizedList(new LinkedList<Event>());
    private final Consumer<String> notifyEventName = name -> events.add(Event.named(name));

    @Test
    public void whenATestConstructorThrows_notifiesListenersOfAFailedTest() {
      runTests(JSpecExamples.FaultyConstructor.class);
      assertThat(getEventNames(), contains("testStarted", "testFailure", "testFinished"));
    }

    @Test
    public void whenATestThunkThrows_notifiesListenersOfAFailedTest() {
      runTests(JSpecExamples.FailingTest.class);
      assertThat(getEventNames(), contains("testStarted", "testFailure", "testFinished"));
    }
    
    public class givenATestInAnItField {
      @Before
      public void setupTestExecutionSpy() {
        JSpecExamples.One.setEventListener(notifyEventName);
        runTests(JSpecExamples.One.class);
      }
      
      @After
      public void recallSpies() {
        JSpecExamples.One.setEventListener(null);
      }
      
      @Test
      public void notifiesStartThenConstructsAndRunsTheTestThenNotifiesFinish() {
        assertThat(getEventNames(), 
          contains("testStarted", "JSpecExamples.One::new", "JSpecExamples.One::only_test", "testFinished"));
      }
    }
    
    public class givenMultipleTestsAndOneOrMoreFail {
      @Before
      public void setupTestExecutionSpy() {
        JSpecExamples.OnePassOneFail.setEventListener(notifyEventName);
        runTests(JSpecExamples.OnePassOneFail.class);
      }
      
      @After
      public void recallSpies() {
        JSpecExamples.OnePassOneFail.setEventListener(null);
      }
      
      @Test
      public void runsRemainingTests() {
        assertThat(getEventNames(),
          hasItems("JSpecExamples.OnePassOneFail::fail", "JSpecExamples.OnePassOneFail::pass"));
      }
    }
    
    private void runTests(Class<?> testClass) {
      RunNotifier notifier = new RunNotifier();
      notifier.addListener(new RunListenerSpy(events::add));
      JSpecRunner runner = runnerFor(testClass);
      runner.run(notifier);
    }
    
    private List<String> getEventNames() {
      return events.stream().map(x -> x.name).collect(Collectors.toList());
    }
  }
  
  private static JSpecRunner runnerFor(Class<?> testClass) {
    try {
      return new JSpecRunner(testClass);
    } catch (InitializationError e) {
      System.out.println("\nInitialization error(s)");
      unearthInitializationErrors(e).forEach(x -> {
        System.out.printf("[%s]\n", x.getClass());
        x.printStackTrace(System.out);
      });
      fail("Failed to create JSpecRunner");
      return null;
    }
  }
  
  private static Stream<InitializationError> unearthInitializationErrors(InitializationError bigKahuna) {
    List<InitializationError> acc = new LinkedList<InitializationError>();
    Stack<InitializationError> toVisit = new Stack<InitializationError>();
    toVisit.push(bigKahuna);
    while(!toVisit.isEmpty()) {
      InitializationError parent = toVisit.pop();
      acc.add(parent);
      parent.getCauses().stream()
        .filter(x -> x instanceof InitializationError)
        .forEach(x -> toVisit.push((InitializationError) x));
    }
    return acc.stream();
  }
}