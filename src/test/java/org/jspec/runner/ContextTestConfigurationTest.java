package org.jspec.runner;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.jspec.util.Assertions.assertThrows;
import static org.junit.Assert.assertThat;

import java.util.Collections;
import java.util.List;

import org.jspec.proto.JSpecExamples;
import org.jspec.runner.ContextTestConfiguration.NoExamplesException;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class ContextTestConfigurationTest {
  public class getContextClass {
    private final TestConfiguration subject = ContextTestConfiguration.forClass(JSpecExamples.One.class);

    @Test
    public void givenAClass_returnsTheClass() {
      assertThat(subject.getContextClass(), equalTo(JSpecExamples.One.class));
    }
  }

  public class givenAClassWithNoItFields {
    private final TestConfiguration subject = ContextTestConfiguration.forClass(JSpecExamples.Empty.class);

    @Test
    public void findsInitializationErrors_containsNoExamplesException() {
      assertThat(subject.findInitializationErrors().stream().map(x -> x.getClass()).collect(toList()),
        contains(NoExamplesException.class));
    }

    @Test
    public void getExamples_throwsNoExamplesException() {
      assertThrows(NoExamplesException.class,
        equalTo("Test context org.jspec.proto.JSpecExamples$Empty must contain at least 1 example in an It field"), 
        subject::getExamples);
    }
    
    @Test
    public void hasInitializationErrors_returnsTrue() {
      assertThat(subject.hasInitializationErrors(), equalTo(true));
    }
  }

  public class givenAClassWithOneOrMoreItFields {
    private final TestConfiguration subject = ContextTestConfiguration.forClass(JSpecExamples.Two.class);

    @Test
    public void findInitializationErrors_returnsEmptyList() {
      assertThat(subject.findInitializationErrors(), equalTo(Collections.emptyList()));
    }

    @Test
    public void getExamples_returnsAFieldExampleForEachItField() {
      List<Example> examples = subject.getExamples().collect(toList());
      assertThat(examples.stream().map(Example::getClass).collect(toList()),
        contains(FieldExample.class, FieldExample.class));
      assertThat(examples.stream().map(Example::describeBehavior).collect(toList()),
        contains("first_test", "second_test"));
    }
    
    @Test
    public void hasInitializationErrors_returnsFalse() {
      assertThat(subject.hasInitializationErrors(), equalTo(false));
    }
  }
}