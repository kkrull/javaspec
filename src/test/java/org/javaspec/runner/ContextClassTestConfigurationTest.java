package org.javaspec.runner;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.javaspec.util.Assertions.assertListEquals;
import static org.javaspec.util.Assertions.assertThrows;
import static org.junit.Assert.assertThat;

import java.util.Collections;
import java.util.List;

import org.javaspec.proto.JSpecExamples;
import org.javaspec.runner.ContextClassTestConfiguration;
import org.javaspec.runner.Example;
import org.javaspec.runner.FieldExample;
import org.javaspec.runner.TestConfiguration;
import org.javaspec.runner.ContextClassTestConfiguration.NoExamplesException;
import org.javaspec.runner.ContextClassTestConfiguration.UnknownStepExecutionSequenceException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableList;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class ContextClassTestConfigurationTest {
  public class findInitializationErrors {
    @Test
    public void givenAClassWithNoItFields_containsNoExamplesException() {
      shouldFindInitializationError(JSpecExamples.Empty.class, NoExamplesException.class,
        "Test context org.javaspec.proto.JSpecExamples$Empty must contain at least 1 example in an It field");
    }
    
    @Test
    public void givenAClassWith2OrMoreEstablishFields_containsUnknownStepExecutionSequenceException() {
      shouldFindInitializationError(JSpecExamples.TwoEstablish.class, UnknownStepExecutionSequenceException.class,
        "Impossible to determine running order of multiple Establish functions in test context org.javaspec.proto.JSpecExamples$TwoEstablish");
    }
    
    @Test
    public void givenAClassWith2OrMoreBecauseFields_containsUnknownStepExecutionSequenceException() {
      shouldFindInitializationError(JSpecExamples.TwoBecause.class, UnknownStepExecutionSequenceException.class,
        "Impossible to determine running order of multiple Because functions in test context org.javaspec.proto.JSpecExamples$TwoBecause");
    }
    
    @Test
    public void givenAClassWith2OrMoreCleanupFields_containsUnknownStepExecutionSequenceException() {
      shouldFindInitializationError(JSpecExamples.TwoCleanup.class, UnknownStepExecutionSequenceException.class,
        "Impossible to determine running order of multiple Cleanup functions in test context org.javaspec.proto.JSpecExamples$TwoCleanup");
    }
    
    @Test
    public void givenAClassWith1OrMoreItFieldsAndMeetsRemainingCriteria_returnsEmptyList() {
      TestConfiguration subject = new ContextClassTestConfiguration(JSpecExamples.TwoIt.class);
      assertThat(subject.findInitializationErrors(), equalTo(Collections.emptyList()));
    }
    
    private void shouldFindInitializationError(Class<?> contextType, Class<?> errorType, String errorMsg) {
      TestConfiguration subject = new ContextClassTestConfiguration(contextType);
      assertThat(subject.findInitializationErrors().stream().map(x -> x.getClass()).collect(toList()),
        contains(equalTo(errorType)));
      assertThat(subject.findInitializationErrors().stream().map(Throwable::getMessage).collect(toList()),
        contains(equalTo(errorMsg)));
    }
  }
  
  public class getContextClass {
    @Test
    public void returnsTheGivenClass() {
      TestConfiguration subject = new ContextClassTestConfiguration(JSpecExamples.TwoIt.class);
      assertThat(subject.getContextClass(), equalTo(JSpecExamples.TwoIt.class));
    }
  }
  
  public class getExamples {
    public class givenAClassWith1OrMoreInitializationErrors {
      private final TestConfiguration subject = new ContextClassTestConfiguration(JSpecExamples.Empty.class);
      
      @Test
      public void throwsIllegalStateExceptionContainingOneOfThem() {
        assertThrows(IllegalStateException.class,
          equalTo("Test context org.javaspec.proto.JSpecExamples$Empty has one or more initialization errors"), 
          NoExamplesException.class,
          subject::getExamples);
      }
    }
    
    public class givenAClassWithNoFixtureFields {
      private final TestConfiguration subject = new ContextClassTestConfiguration(JSpecExamples.OneIt.class);
      
      @Test
      public void createsEachExampleWithoutFixtureMethods() {
        assertThat(subject.getExamples().map(Example::describeSetup).collect(toList()), contains(equalTo("")));
        assertThat(subject.getExamples().map(Example::describeAction).collect(toList()), contains(equalTo("")));
        assertThat(subject.getExamples().map(Example::describeCleanup).collect(toList()), contains(equalTo("")));
      }
    }
    
    public class givenAClassWithUpToOneOfEachTypeOfFixtureField {
      private final TestConfiguration subject = new ContextClassTestConfiguration(JSpecExamples.FullFixture.class);
      
      @Test
      public void associatesAnEstablishFieldWithEachExample() {
        assertListEquals(ImmutableList.of("arranges"),
          subject.getExamples().map(Example::describeSetup).collect(toList()));
      }
      
      @Test
      public void associatesABecauseFieldWithEachExample() {
        assertListEquals(ImmutableList.of("acts"),
          subject.getExamples().map(Example::describeAction).collect(toList()));
      }
      
      @Test
      public void associatesACleanupFieldWithEachExample() {
        assertListEquals(ImmutableList.of("cleans"),
          subject.getExamples().map(Example::describeCleanup).collect(toList()));
      }
    }
    
    public class givenAClassWith1OrMoreItFields {
      private final TestConfiguration subject = new ContextClassTestConfiguration(JSpecExamples.TwoItWithEstablish.class);
      private List<Example> examples;
      
      @Before
      public void readExamples() {
        this.examples = subject.getExamples().collect(toList());
      }
      
      @Test
      public void returnsAFieldExampleForEachItField() {
        assertThat(examples.stream().map(Example::describeBehavior).collect(toList()),
          contains("does_one_thing", "does_something_else"));
        examples.stream().map(Example::getClass).forEach(x -> assertThat(x, equalTo(FieldExample.class)));
      }
      
      @Test
      public void associatesAnyFixtureMethodsWithEachExample() {
        assertListEquals(ImmutableList.of("that", "that"), 
          examples.stream().map(Example::describeSetup).collect(toList()));
      }
    }
  }
}