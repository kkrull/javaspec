package org.jspec.runner;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.jspec.util.Assertions.assertThrows;
import static org.junit.Assert.assertThat;

import java.util.LinkedList;
import java.util.List;

import org.jspec.dsl.It;
import org.jspec.proto.JSpecExamples;
import org.jspec.runner.FieldExample.UnsupportedFieldException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class FieldExampleTest {
  public class describeBehavior {
    private final Example subject;
    It describesADesiredBehavior;
    
    public describeBehavior() throws Exception {
      this.subject = new FieldExample(getClass().getDeclaredField("describesADesiredBehavior"));
    }
    
    @Test
    public void returnsTheNameOfTheField() {
      assertThat(subject.describeBehavior(), is("describesADesiredBehavior"));
    }
  }
  
  public class run {
    public class givenABehaviorFieldOfSomeOtherType {
      private final Class<?> contextClass = UnsupportedBehaviorField.class;
      private final Example subject;
      
      public givenABehaviorFieldOfSomeOtherType() throws Exception {
        this.subject = new FieldExample(contextClass.getDeclaredField("notAnItField"));
      }
      
      @Test
      public void throwsUnsupportedFieldException() {
        assertThrows(UnsupportedFieldException.class, 
          String.format("Invalid type for %s.notAnItField: java.lang.Integer", contextClass.getName()),
          subject::run);
      }
    }
    
    public class givenAnItField {
      private final Example subject;
      private final List<String> events = new LinkedList<String>();
      
      public givenAnItField() throws Exception {
        this.subject = new FieldExample(JSpecExamples.One.class.getDeclaredField("only_test"));
      }
      
      @Before
      public void spy() {
        JSpecExamples.One.setEventListener(events::add);
        this.subject.run();
      }
      
      @After
      public void releaseSpy() {
        JSpecExamples.One.setEventListener(null);
      }
      
      @Test
      public void constructsTheContextClassThenRunsTheFunctionAssignedToTheGivenField() {
        assertThat(events, contains("JSpecExamples.One::new", "JSpecExamples.One::only_test"));
      }
    }
  }
  
  public static class UnsupportedBehaviorField {
    Integer notAnItField = 42;
  }
}