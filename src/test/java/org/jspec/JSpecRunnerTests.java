package org.jspec;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.jspec.dsl.It;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;

import static com.google.common.collect.Lists.*;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

@RunWith(HierarchicalContextRunner.class)
public class JSpecRunnerTests {
  public class constructor {
    @Test
    public void givenAClassWithNoItFields_raisesInitializationError() {
      try {
        new JSpecRunner(NoTests.class);
      } catch (InitializationError ex) {
        //InitializationError buries the message so deep, it's useless (read: no assertion on the detail message)
        return;
      }
      fail("Expected InitializationError");
    }
    
    class NoTests {}
  }
  
  public class getDescription {
    @Test
    public void givenAClass_hasTheGivenTestClass() {
      assertEquals(TwoTests.class, descriptionOf(TwoTests.class).getTestClass());
    }

    @Test
    public void givenAClassWithoutAnnotations_hasEmptyAnnotations() {
      assertEquals(Collections.emptyList(), descriptionOf(TwoTests.class).getAnnotations());
    }

    @Test
    public void givenAClassWithAnnotations_hasThoseAnnotations() {
      assertNotNull(descriptionOf(IgnoredTests.class).getAnnotation(Ignore.class));
    }

    public class givenAClassWith1OrMoreItFields {
      Description description = descriptionOf(TwoTests.class);

      @Test
      public void hasAChildForEach() {
        List<Description> children = description.getChildren();
        List<String> names = children.stream().map(Description::getDisplayName).sorted().collect(Collectors.toList());
        assertEquals(newArrayList(
          "first_test(org.jspec.JSpecRunnerTests$TwoTests)",
          "second_test(org.jspec.JSpecRunnerTests$TwoTests)"),
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
    public class givenAClassWith1OrMoreItFields {

      @Test
      public void notifiesListenersWhenTestsStartAndFinish() {
        RunNotifier notifier = new RunNotifier();
        RunListenerSpy listener = new RunListenerSpy();
        notifier.addListener(listener);
        JSpecRunner runner = runnerFor(OneTest.class);
        runner.run(notifier);
        
        assertEquals(newArrayList("testStarted", "testFinished"), listener.notifications);
      }
      
      @Test @Ignore
      public void runsTheTest() {
        fail("pending");
      }
      
      @Test @Ignore
      public void notifiesListenersOfTestFailure() { }
      
      @Test @Ignore
      public void notifiesListenersOfIgnoredTests() { }
    }
  }
  
  static JSpecRunner runnerFor(Class<?> testClass) {
    try {
      return new JSpecRunner(testClass);
    } catch (InitializationError e) {
      throw new RuntimeException(e);
    }
  }

  @Ignore
  class IgnoredTests {
    It gets_ignored = () -> assertEquals(1, 2);
  }
  
  class OneTest {
    It only_test = () -> assertEquals(1, 1);
  }

  class TwoTests {
    It first_test = () -> assertEquals(1, 1);
    It second_test = () -> assertEquals(2, 2);
  }
}