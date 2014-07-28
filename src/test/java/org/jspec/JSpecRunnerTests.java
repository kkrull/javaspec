package org.jspec;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.jspec.dsl.It;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;

public class JSpecRunnerTests {

  protected final List<String> testSequence = new LinkedList<String>();
  
  static Description descriptionOf(Class<?> testClass) {
    JSpecRunner runner = runnerFor(testClass);
    return runner.getDescription();
  }
  
  static void runTests(Class<?> testClass) {
    JSpecRunner runner = runnerFor(testClass);
    runner.run(new RunNotifier());
  }
  
  static JSpecRunner runnerFor(Class<?> testClass) {
    try {
      return new JSpecRunner(testClass);
    } catch (InitializationError e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void getDescription_describesTheClass() {
    assertEquals(NoTests.class, descriptionOf(NoTests.class).getTestClass());
  }
  
  @Test
  public void getDescription_givenAClassWithUnderscores_convertsDescriptionToWords() {
    assertThat(descriptionOf(describes_a_context.class).getDisplayName(), endsWith("describes a context"));
  }
  
  @Test
  public void getDescription_givenAClassWithAnnotations_includesThoseAnnotations() {
    assertNotNull(descriptionOf(IgnoredTests.class).getAnnotation(Ignore.class));
  }
  
  @Test
  public void run_givenAClassWithNoTests_runsNothing() {
    runTests(NoTests.class);
    assertTestSequence();
  }
  
  @Test
  public void run_givenAClassWithItFields_constructsAndRunsEachTest() {
    runTests(OneTest.class);
    assertTestSequence("constuctor", "test1");
  }
  
  void assertTestSequence(String... testIds) {
    assertEquals(Arrays.asList(testIds), testSequence);
  }
  
  final class describes_a_context {}
  @Ignore final class IgnoredTests {}

  public class NoTests {
    public NoTests() {
      testSequence.add("constructor");
    }
  }
  
  public class OneTest {
    public OneTest() {
      testSequence.add("constructor");
    }
    
    It runs = () -> testSequence.add("test1");
  }
}