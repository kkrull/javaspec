package org.jspec;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.Description;

public class JSpecRunnerTests {

  @Test
  public void givenNull_throwsExcpetion() {
    assertThrows(IllegalArgumentException.class, () -> new JSpecRunner(null));
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
  
  static Description descriptionOf(Class<?> testClass) {
    JSpecRunner runner = runnerFor(testClass);
    return runner.getDescription();
  }
  
  static JSpecRunner runnerFor(Class<?> testClass) { return new JSpecRunner(testClass); }

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