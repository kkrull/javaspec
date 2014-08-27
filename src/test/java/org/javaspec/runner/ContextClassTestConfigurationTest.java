package org.javaspec.runner;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.javaspec.testutil.Assertions.assertListEquals;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.javaspec.proto.ContextClasses;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableList;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class ContextClassTestConfigurationTest {
  public class getExamples {
    public class givenAClassWithNoFixtureFields {
      private final TestConfiguration subject = new ContextClassTestConfiguration(ContextClasses.OneIt.class);
      
      @Test
      public void createsEachExampleWithoutFixtureMethods() {
        assertThat(subject.getExamples().map(Example::describeSetup).collect(toList()), contains(equalTo("")));
        assertThat(subject.getExamples().map(Example::describeAction).collect(toList()), contains(equalTo("")));
        assertThat(subject.getExamples().map(Example::describeCleanup).collect(toList()), contains(equalTo("")));
      }
    }
    
    public class givenAClassWithUpToOneOfEachTypeOfFixtureField {
      private final TestConfiguration subject = new ContextClassTestConfiguration(ContextClasses.FullFixture.class);
      
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
      private final TestConfiguration subject = new ContextClassTestConfiguration(ContextClasses.TwoItWithEstablish.class);
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