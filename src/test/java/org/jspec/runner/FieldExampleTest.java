package org.jspec.runner;

import static org.hamcrest.Matchers.*;
import static org.jspec.util.Assertions.assertThrows;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import org.jspec.dsl.It;
import org.jspec.proto.JSpecExamples;
import org.jspec.runner.FieldExample.UnsupportedConstructorException;
import org.jspec.runner.FieldExample.UnsupportedFieldException;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class FieldExampleTest {
  public class constructor {
    public class givenABehaviorFieldOfSomeOtherType {
      private final Class<?> contextClass = UnsupportedBehaviorField.class;
      private final Field field;
      
      public givenABehaviorFieldOfSomeOtherType() throws Exception {
        this.field = contextClass.getDeclaredField("notAnItField");
      }
      
      @Test
      public void throwsUnsupportedFieldException() {
        assertThrows(UnsupportedFieldException.class, 
          is(String.format("Invalid type for %s.notAnItField: java.lang.Integer", contextClass.getName())),
          () -> new FieldExample(field));
      }
    }
  }
  
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
    protected final List<String> events = new LinkedList<String>();
    
    public class givenAClassWithoutACallableNoArgConstructor {
      @Test
      public void ThrowsUnsupportedConstructorException() throws Exception  {
        assertThrowsUnsupportedConstructorException(JSpecExamples.HiddenConstructor.class, "is_otherwise_valid");
        assertThrowsUnsupportedConstructorException(JSpecExamples.ConstructorHasArguments.class, "is_otherwise_valid");
      }
      
      private void assertThrowsUnsupportedConstructorException(Class<?> context, String itFieldName) throws Exception {
        Field field = context.getDeclaredField(itFieldName);
        Example subject = new FieldExample(field);
        assertThrows(UnsupportedConstructorException.class,
          is(String.format("Unable to find a no-argument constructor for class %s", context.getName())),
          () -> subject.run());
      }
    }
    
    public class givenAFaultyConstructor {
      @Test
      public void failsTheTestByThrowingTestSetupFailedException() {
        fail("pending");
      }
    }
    
    public class givenAnItField {
      @Before
      public void spy() {
        JSpecExamples.One.setEventListener(events::add);
//        this.subject.run();
      }
      
      @After
      public void releaseSpy() {
        JSpecExamples.One.setEventListener(null);
      }
      
      @Test @Ignore("wip")
      public void constructsTheContextClassThenRunsTheFunctionAssignedToTheGivenField() {
        assertThat(events, contains("JSpecExamples.One::new", "JSpecExamples.One::only_test"));
      }
    }
  }
  
  public static class UnsupportedBehaviorField {
    Integer notAnItField;
  }
}