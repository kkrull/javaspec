package org.jspec;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.*;
import static org.jspec.util.Assertions.assertThrows;
import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jspec.JSpecRunner.InvalidConstructorError;
import org.jspec.JSpecRunner.NoExamplesError;
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
public class JSpecRunnerTests {
  public class constructor {
    public class givenAnInvalidTestClass {
      @Test
      public void givenAClassWithoutAPublicConstructor_raisesInvalidConstructorError() {
        assertInitializationError(JSpecTests.HiddenConstructor.class, InvalidConstructorError.class);
      }
      
      @Test
      public void givenAClassWithAPublicConstructorTakingArguments_raisesInvalidConstructorError() {
        assertInitializationError(JSpecTests.PublicConstructorWithArgs.class, InvalidConstructorError.class);
      }

      @Test
      public void givenAClassWithTwoOrMoreConstuctors_raisesIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> runnerFor(JSpecTests.MultiplePublicConstructors.class));
      }
      
      @Test
      public void givenAClassWithNoItFields_raisesNoExamplesError() {
        assertInitializationError(JSpecTests.Empty.class, NoExamplesError.class);
      }
      
      void assertInitializationError(Class<?> context, Class<? extends InitializationError> expected) {
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
    
    public class givenAClassWhoseOnlyPublicConstructorIsNoArg {
      @Test
      public void itDoesNotInvokeTheConstructor() {
        //Allow the test to run and fail instead of bringing down the whole suite
        runnerFor(JSpecTests.FaultyConstructor.class);
      }
    }
  }
  
  public class getDescription {
    @Test
    public void givenAClass_hasTheGivenTestClass() {
      assertEquals(JSpecTests.Two.class, descriptionOf(JSpecTests.Two.class).getTestClass());
    }

    @Test
    public void givenAClassWithoutAnnotations_hasEmptyAnnotations() {
      assertEquals(emptyList(), descriptionOf(JSpecTests.Two.class).getAnnotations());
    }

    @Test
    public void givenAClassWithAnnotations_hasThoseAnnotations() {
      assertNotNull(descriptionOf(JSpecTests.IgnoredClass.class).getAnnotation(Ignore.class));
    }

    public class givenAClassWith1OrMoreItFields {
      final Description description = descriptionOf(JSpecTests.Two.class);

      @Test
      public void hasAChildForEach() {
        List<Description> children = description.getChildren();
        List<String> names = children.stream().map(Description::getDisplayName).sorted().collect(Collectors.toList());
        assertEquals(newArrayList(
          "first_test(org.jspec.JSpecTests$Two)",
          "second_test(org.jspec.JSpecTests$Two)"),
          names);
      }

      @Test
      public void hasTestCountForEachField() {
        assertEquals(2, description.testCount());
      }
    }

    Description descriptionOf(Class<?> testClass) {
      JSpecRunner runner = runnerFor(testClass);
      return runner.getDescription();
    }
  }

  public class run {
    final List<String> notifications = new LinkedList<String>();

    public class givenAClassWith1OrMoreItFields {
      
      @Before
      public void setupTestExecutionSpy() {
        JSpecTests.One.notifyEvent = notifications::add;
      }
      
      @After
      public void recallSpies() {
        JSpecTests.One.notifyEvent = null;
      }
      
      @Test
      public void runsTheTest() {
        assertThat(
          runtimeEvents(JSpecTests.One.class),
          hasItem("JSpecTests.One::only_test"));
      }
      
      @Test
      @SuppressWarnings("unchecked")
      public void notifiesListenersWhenTestsStartAndFinish() {
        assertThat(
          runtimeEvents(JSpecTests.One.class),
          contains(is("testStarted"), anything(), is("testFinished")));
      }
      
      @Test @Ignore
      public void givenABadTestConstructor_failsTheTest() {
        fail("pending");
      }
      
      @Test
      public void givenAFailingTest_runsAllTestsInTheClass() { }
      
      @Test @Ignore
      public void notifiesStartRunsThenNotifiesFinish() { }
      
      @Test @Ignore
      public void notifiesListenersOfTestFailure() { }
      
      @Test @Ignore
      public void whenATestConstructorThrowsAnException_reportsTheException() { }
    }
    
    List<String> runtimeEvents(Class<?> testClass) {
      RunNotifier notifier = new RunNotifier();
      notifier.addListener(new RunListenerSpy(notifications::add));
      JSpecRunner runner = runnerFor(testClass);
      runner.run(notifier);
      return notifications;
    }
  }
  
  static JSpecRunner runnerFor(Class<?> testClass) {
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
  
  static Stream<InitializationError> unearthInitializationErrors(InitializationError bigKahuna) {
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