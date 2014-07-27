package org.jspec;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runners.model.InitializationError;

public class JSpecRunnerTests {

  static Description descriptionOf(Class<?> testClass) {
    JSpecRunner runner = runnerFor(testClass);
    return runner.getDescription();
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
  public void run_givenAClassWithNoItFields_runsNoTests() {
    fail("pending");
  }
  
  final class NoTests {}
  final class describes_a_context {}
  @Ignore final class IgnoredTests {}

  static void assertThrows(Class<? extends Exception> exceptionClass, Thunk thunk) {
    try {
      thunk.run();
    } catch (Exception e) {
      assertEquals("Unexpected type of exception thrown", exceptionClass, e.getClass());
      return;
    }

    fail(String.format("Expected %s to be thrown, but no exception was thrown", exceptionClass));
  }
  
  @FunctionalInterface
  interface Thunk {
    void run();
  }
}