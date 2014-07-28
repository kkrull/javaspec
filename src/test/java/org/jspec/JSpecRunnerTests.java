package org.jspec;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collections;
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
  
  /* getDescription */

  @Test
  public void getDescription_hasTheGivenTestClass() {
    assertEquals(NoTests.class, descriptionOf(NoTests.class).getTestClass());
  }
  
  @Test
  public void getDescription_givenAClassWithoutAnnotations_hasEmptyAnnotations() {
    assertEquals(Collections.emptyList(), descriptionOf(NoTests.class).getAnnotations());
  }
  
  @Test
  public void getDescription_givenAClassWithAnnotations_hasThoseAnnotations() {
    assertNotNull(descriptionOf(IgnoredTests.class).getAnnotation(Ignore.class));
  }
  
  @Test
  public void getDescription_givenAClassWithNoItFields_hasNoChildren() {
    assertEquals(Collections.emptyList(), descriptionOf(NoTests.class).getChildren());
  }
  
  @Test
  public void getDescription_givenAClassWith1OrMoreItFields_hasAChildForEach() {
    assertEquals(1, descriptionOf(OneTest.class).getChildren().size());
  }
  
  @Ignore final class IgnoredTests {}

  public class NoTests {
    public NoTests() {}
  }
  
  public class OneTest {
    public OneTest() {}
    It runs = () -> assertEquals(1, 1);
  }
}