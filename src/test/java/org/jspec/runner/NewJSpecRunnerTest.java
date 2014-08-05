package org.jspec.runner;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.jspec.util.Assertions.assertListEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jspec.proto.JSpecExamples;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.model.InitializationError;

import com.google.common.collect.ImmutableList;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class NewJSpecRunnerTest {
  public class constructor {
    @Test
    public void givenAConfigurationWithoutErrors_raisesNoError() {
      runnerFor(configFinding());
    }

    @Test
    public void givenAConfigurationWith1OrMoreErrors_raisesInitializationErrorWithThoseErrors() {
      TestConfiguration config = configFinding(new IllegalArgumentException(), new AssertionError());
      assertInitializationError(config, ImmutableList.of(IllegalArgumentException.class, AssertionError.class));
    }
    
    @Test
    public void givenAContextClassSuitableForJSpecButNotForJUnit_raisesNoError() {
      runnerFor(JSpecExamples.MultiplePublicConstructors.class);
    }

    private void assertInitializationError(TestConfiguration config, List<Class<? extends Throwable>> expectedCauses) {
      try {
        new NewJSpecRunner(config);
      } catch (InitializationError ex) {
        assertListEquals(expectedCauses, flattenCauses(ex).map(Throwable::getClass).collect(Collectors.toList()));
        return;
      }
      fail(String.format("Expected causes of initialization error to be <%s>, but nothing was thrown", expectedCauses));
    }
  }
  
  public class getDescription {
    public class givenAContextClass {
      private final NewJSpecRunner runner = runnerFor(JSpecExamples.IgnoredClass.class);
      private final Description description = runner.getDescription();
      
      @Test
      public void describesTheConfiguredClass() {
        assertThat(description.getTestClass(), equalTo(JSpecExamples.IgnoredClass.class));
        assertThat(description.getAnnotation(Ignore.class), notNullValue());
      }
    }
    
    public class givenATestConfiguration {
      private final NewJSpecRunner runner = runnerFor(configOf(JSpecExamples.IgnoredClass.class));
      private final Description description = runner.getDescription();
      
      @Test
      public void describesTheConfiguredClass() {
        assertThat(description.getTestClass(), equalTo(JSpecExamples.IgnoredClass.class));
        assertThat(description.getAnnotation(Ignore.class), notNullValue());
      }
    }
  
    public class givenATestConfigurationWith1OrMoreChildren {
      @Test
      public void describesChildrenUsingTheProtectedMethods() {
        fail("pending");
      }
    }
  }
  
  public class run {
    public class givenAContextClass {
      @Test @Ignore
      public void createsAClassTestConfigurationForTheGivenClass() {
        fail("pending");
      }
    }
  }
  
  private static TestConfiguration configOf(Class<?> contextClass) {
    return new TestConfiguration() {
      @Override
      public List<Throwable> findInitializationErrors() { return Collections.emptyList(); }

      @Override
      public boolean hasInitializationErrors() { return false; }
      
      @Override
      public Class<?> getContextClass() { return contextClass; }
    };
  }
  
  private static TestConfiguration configFinding(Throwable... errors) {
    return new TestConfiguration() {
      @Override
      public List<Throwable> findInitializationErrors() { return Arrays.asList(errors); }

      @Override
      public boolean hasInitializationErrors() { return errors.length > 0; }

      @Override
      public Class<?> getContextClass() { return JSpecExamples.One.class; }
    };
  }
    
  private static NewJSpecRunner runnerFor(Class<?> contextClass) {
    try {
      return new NewJSpecRunner(contextClass);
    } catch (InitializationError e) {
      return failForInitializationError(e);
    }
  }

  private static NewJSpecRunner runnerFor(TestConfiguration config) {
    try {
      return new NewJSpecRunner(config);
    } catch (InitializationError e) {
      return failForInitializationError(e);
    }
  }

  private static NewJSpecRunner failForInitializationError(InitializationError e) {
    System.out.println("\nInitialization error(s)");
    flattenCauses(e).forEach(x -> {
      System.out.printf("[%s]\n", x.getClass());
      x.printStackTrace(System.out);
    });
    fail("Failed to create JSpecRunner");
    return null;
  }

  private static Stream<Throwable> flattenCauses(InitializationError root) {
    List<Throwable> causes = new LinkedList<Throwable>();
    Stack<InitializationError> nodesWithChildren = new Stack<InitializationError>();
    nodesWithChildren.push(root);
    while (!nodesWithChildren.isEmpty()) {
      InitializationError parent = nodesWithChildren.pop();
      for(Throwable child : parent.getCauses()) {
        if(child instanceof InitializationError) {
          nodesWithChildren.push((InitializationError) child);
        } else {
          causes.add(child);
        }
      }
    }
    return causes.stream();
  }
}