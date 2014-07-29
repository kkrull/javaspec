package org.jspec;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.jspec.dsl.It;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.model.InitializationError;

import com.google.common.collect.Lists;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class JSpecRunnerTests {
  public class getDescription {
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

    public class givenAClassWithNoItFields {
      Description description = descriptionOf(NoTests.class);

      @Test
      public void hasNoChildren() {
        assertEquals(Collections.emptyList(), description.getChildren());
      }

      @Test
      public void hasTestCount0() {
        assertEquals(0, description.testCount());
      }
    }

    public class givenAClassWith1OrMoreItFields {
      Description description = descriptionOf(TwoTests.class);

      @Test
      public void hasAChildForEach() {
        List<Description> children = description.getChildren();
        List<String> names = children.stream().map(Description::getDisplayName).sorted().collect(Collectors.toList());
        assertEquals(Lists.newArrayList(
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

  static JSpecRunner runnerFor(Class<?> testClass) {
    try {
      return new JSpecRunner(testClass);
    } catch (InitializationError e) {
      throw new RuntimeException(e);
    }
  }

  @Ignore
  class IgnoredTests {}

  class NoTests {}

  class TwoTests {
    It first_test = () -> assertEquals(1, 1);
    It second_test = () -> assertEquals(2, 2);
  }
}