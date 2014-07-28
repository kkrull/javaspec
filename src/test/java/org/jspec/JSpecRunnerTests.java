package org.jspec;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.jspec.dsl.It;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.model.InitializationError;

import com.google.common.collect.Lists;

@RunWith(Enclosed.class)
public class JSpecRunnerTests {
  public static class getDescription {
    static Description descriptionOf(Class<?> testClass) {
      JSpecRunner runner = runnerFor(testClass);
      return runner.getDescription();
    }
    
    @Test
    public void givenAClass_hasTheGivenTestClass() {
      assertEquals(NoTests.class, descriptionOf(NoTests.class).getTestClass());
    }
    
    @Test
    public void givenAClassWithoutAnnotations_hasEmptyAnnotations() {
      assertEquals(Collections.emptyList(), descriptionOf(NoTests.class).getAnnotations());
    }
    
    @Test
    public void givenAClassWithAnnotations_hasThoseAnnotations() {
      assertNotNull(descriptionOf(IgnoredTests.class).getAnnotation(Ignore.class));
    }
    
    @Test
    public void givenAClassWithNoItFields_hasNoChildren() {
      assertEquals(Collections.emptyList(), descriptionOf(NoTests.class).getChildren());
    }
    
    @Test
    public void givenAClassWith1OrMoreItFields_hasAChildForEach() {
      List<Description> children = descriptionOf(TwoTests.class).getChildren();
      List<String> names = children.stream().map(Description::getDisplayName).sorted().collect(Collectors.toList());
      assertEquals(Lists.newArrayList(
        "first_test(org.jspec.JSpecRunnerTests$TwoTests)",
        "second_test(org.jspec.JSpecRunnerTests$TwoTests)"),
        names);
    }
  }
  
  public static class getChildren {
    @Test
    public void works() {
      fail("pending");
    }
  }
  
  static JSpecRunner runnerFor(Class<?> testClass) {
    try {
      return new JSpecRunner(testClass);
    } catch (InitializationError e) {
      throw new RuntimeException(e);
    }
  }
  
  @Ignore class IgnoredTests {}

  class NoTests {
    public NoTests() {}
  }
  
  class TwoTests {
    public TwoTests() {}
    It first_test = () -> assertEquals(1, 1);
    It second_test = () -> assertEquals(2, 2);
  }
}