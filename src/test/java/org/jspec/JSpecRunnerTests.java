package org.jspec;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;

import static com.google.common.collect.Lists.*;
import de.bechte.junit.runners.context.HierarchicalContextRunner;

import static org.junit.Assert.*;

@RunWith(HierarchicalContextRunner.class)
public class JSpecRunnerTests {
  public class constructor {
    @Test
    public void givenAClassWithNoItFields_raisesInitializationError() {
      try {
        new JSpecRunner(JSpecTests.Empty.class);
      } catch (InitializationError ex) {
        //InitializationError buries the message so deep, it's useless (read: no assertion on the detail message)
        return;
      }
      fail("Expected InitializationError");
    }
    
    @Test @Ignore
    public void givenAClassWithAnyOtherConstructor_raisesInitializationError() { }
  }
  
  public class getDescription {
    @Test
    public void givenAClass_hasTheGivenTestClass() {
      assertEquals(JSpecTests.Two.class, descriptionOf(JSpecTests.Two.class).getTestClass());
    }

    @Test
    public void givenAClassWithoutAnnotations_hasEmptyAnnotations() {
      assertEquals(Collections.emptyList(), descriptionOf(JSpecTests.Two.class).getAnnotations());
    }

    @Test
    public void givenAClassWithAnnotations_hasThoseAnnotations() {
      assertNotNull(descriptionOf(JSpecTests.IgnoredClass.class).getAnnotation(Ignore.class));
    }

    public class givenAClassWith1OrMoreItFields {
      Description description = descriptionOf(JSpecTests.Two.class);

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
    public class givenAClassWith1OrMoreItFields {
      
      final List<String> notifications = new LinkedList<String>();

      @Test @Ignore("wip")
      public void runsTheTest() {
        RunNotifier notifier = new RunNotifier();
        RunListenerSpy listener = new RunListenerSpy(notifications);
        notifier.addListener(listener);
        JSpecRunner runner = runnerFor(JSpecTests.One.class);
        runner.run(notifier);
        
        assertThat(notifications, Matchers.hasItem("JSpecTests$One.only_test::run"));
      }
      
      @Test
      public void notifiesListenersWhenTestsStartAndFinish() {
        RunNotifier notifier = new RunNotifier();
        RunListenerSpy listener = new RunListenerSpy();
        notifier.addListener(listener);
        JSpecRunner runner = runnerFor(JSpecTests.One.class);
        runner.run(notifier);
        
        assertEquals(newArrayList("testStarted", "testFinished"), listener.notifications);
      }
      
      @Test @Ignore
      public void notifiesStartRunsThenNotifiesFinish() { }
      
      @Test @Ignore
      public void notifiesListenersOfTestFailure() { }
      
      @Test @Ignore
      public void notifiesListenersOfIgnoredTests() { }
      
      @Test @Ignore
      public void whenATestConstructorThrowsAnException_reportsTheException() { }
    }
  }
  
  static JSpecRunner runnerFor(Class<?> testClass) {
    try {
      return new JSpecRunner(testClass);
    } catch (InitializationError e) {
      throw new RuntimeException(e);
    }
  }
}