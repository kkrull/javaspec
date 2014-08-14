package org.jspec.runner;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.jspec.util.Assertions.assertListEquals;
import static org.jspec.util.Assertions.assertThrows;
import static org.junit.Assert.assertThat;

import java.util.Collections;
import java.util.List;

import org.jspec.proto.JSpecExamples;
import org.jspec.runner.ContextTestConfiguration.NoExamplesException;
import org.jspec.runner.ContextTestConfiguration.UnknownStepExecutionSequenceException;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableList;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class ContextTestConfigurationTest {
  public class findInitializationErrors {
    @Test
    public void givenAClassWithNoItFields_containsNoExamplesException() {
      TestConfiguration subject = new ContextTestConfiguration(JSpecExamples.Empty.class);
      assertThat(subject.findInitializationErrors().stream().map(x -> x.getClass()).collect(toList()),
        contains(equalTo(NoExamplesException.class)));
      assertThat(subject.findInitializationErrors().stream().map(Throwable::getMessage).collect(toList()),
        contains(equalTo("Test context org.jspec.proto.JSpecExamples$Empty must contain at least 1 example in an It field")));
    }
    
    @Test
    public void givenAClassWith2OrMoreEstablishFields_containsUnknownStepExecutionSequenceException() {
      TestConfiguration subject = new ContextTestConfiguration(JSpecExamples.EstablishTwice.class);
      assertThat(subject.findInitializationErrors().stream().map(x -> x.getClass()).collect(toList()),
        contains(equalTo(UnknownStepExecutionSequenceException.class)));
      assertThat(subject.findInitializationErrors().stream().map(Throwable::getMessage).collect(toList()),
        contains(equalTo("Impossible to determine running order of multiple Establish functions in test context org.jspec.proto.JSpecExamples$EstablishTwice")));
    }
    
    @Test
    public void givenAClassWith2OrMoreBecauseFields_containsUnknownStepExecutionSequenceException() {
      TestConfiguration subject = new ContextTestConfiguration(JSpecExamples.BecauseTwice.class);
      assertThat(subject.findInitializationErrors().stream().map(x -> x.getClass()).collect(toList()),
        contains(equalTo(UnknownStepExecutionSequenceException.class)));
      assertThat(subject.findInitializationErrors().stream().map(Throwable::getMessage).collect(toList()),
        contains(equalTo("Impossible to determine running order of multiple Because functions in test context org.jspec.proto.JSpecExamples$BecauseTwice")));
    }
    
    @Test
    public void givenAClassWith1OrMoreItFieldsAndMeetsRemainingCriteria_returnsEmptyList() {
      TestConfiguration subject = new ContextTestConfiguration(JSpecExamples.Two.class);
      assertThat(subject.findInitializationErrors(), equalTo(Collections.emptyList()));
    }
  }
  
  public class getContextClass {
    @Test
    public void returnsTheGivenClass() {
      TestConfiguration subject = new ContextTestConfiguration(JSpecExamples.Two.class);
      assertThat(subject.getContextClass(), equalTo(JSpecExamples.Two.class));
    }
  }
  
  public class getExamples {
    public class givenAClassWith1OrMoreInitializationErrors {
      private final TestConfiguration subject = new ContextTestConfiguration(JSpecExamples.Empty.class);
      
      @Test
      public void throwsIllegalStateExceptionContainingOneOfThem() {
        assertThrows(IllegalStateException.class,
          equalTo("Test context org.jspec.proto.JSpecExamples$Empty has one or more initialization errors"), 
          NoExamplesException.class,
          subject::getExamples);
      }
    }
    
    public class givenAClassWithNoFixtureFields {
      private final TestConfiguration subject = new ContextTestConfiguration(JSpecExamples.One.class);
      
      @Test
      public void createsEachExampleWithoutAnArrangeField() {
        assertThat(subject.getExamples().map(Example::describeSetup).collect(toList()), contains(equalTo("")));
      }
      
      @Test
      public void createsEachExampleWithoutAnActField() {
        assertThat(subject.getExamples().map(Example::describeAction).collect(toList()), contains(equalTo("")));
      }
    }
    
    public class givenAClassWith1EstablishField {
      @Test
      public void associatesTheEstablishFieldWithEachExample() {
        TestConfiguration subject = new ContextTestConfiguration(JSpecExamples.EstablishOnceRunTwice.class);
        assertListEquals(ImmutableList.of("that", "that"),
          subject.getExamples().map(Example::describeSetup).collect(toList()));
      }
    }
    
    public class givenAClassWith1BecauseField {
      @Test
      public void associatesTheEstablishFieldWithEachExample() {
        TestConfiguration subject = new ContextTestConfiguration(JSpecExamples.FullFixture.class);
        assertListEquals(ImmutableList.of("acts"),
          subject.getExamples().map(Example::describeAction).collect(toList()));
      }
    }
    
    public class givenAClassWith1OrMoreItFields {
      private final TestConfiguration subject = new ContextTestConfiguration(JSpecExamples.Two.class);
      
      @Test
      public void returnsAFieldExampleForEachItField() {
        List<Example> examples = subject.getExamples().collect(toList());
        assertThat(examples.stream().map(Example::describeBehavior).collect(toList()),
          contains("first_test", "second_test"));
        examples.stream().map(Example::getClass).forEach(x -> assertThat(x, equalTo(FieldExample.class)));
      }
    }
  }
}