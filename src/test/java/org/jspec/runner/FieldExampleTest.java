package org.jspec.runner;

import static org.hamcrest.Matchers.*;
import static org.jspec.util.Assertions.assertThrows;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import org.jspec.dsl.Because;
import org.jspec.dsl.Establish;
import org.jspec.dsl.It;
import org.jspec.proto.JSpecExamples;
import org.jspec.runner.FieldExample.TestSetupException;
import org.jspec.runner.FieldExample.UnsupportedConstructorException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class FieldExampleTest {
  public class describeAction {
    Because somethingIDo;
    It describesADesiredBehavior;
    
    @Test
    public void givenNoSetup_returnsEmptyString() throws Exception {
      Example subject = new FieldExample(null, null, getClass().getDeclaredField("describesADesiredBehavior"));
      assertThat(subject.describeBehavior(), equalTo("describesADesiredBehavior"));
    }
    
    @Test
    public void givenAField_returnsTheNameOfTheField() throws Exception {
      Example subject = new FieldExample(null, getClass().getDeclaredField("somethingIDo"),
        getClass().getDeclaredField("describesADesiredBehavior"));
      assertThat(subject.describeAction(), equalTo("somethingIDo"));
    }
  }
  
  public class describeBehavior {
    It describesADesiredBehavior;
    
    @Test
    public void returnsTheNameOfTheField() throws Exception {
      Example subject = new FieldExample(null, null, getClass().getDeclaredField("describesADesiredBehavior"));
      assertThat(subject.describeBehavior(), equalTo("describesADesiredBehavior"));
    }
  }
  
  public class describeSetup {
    Establish somethingINeedToRunMyTest;
    It describesADesiredBehavior;
    
    @Test
    public void givenNoSetup_returnsEmptyString() throws Exception {
      Example subject = new FieldExample(null, null, getClass().getDeclaredField("describesADesiredBehavior"));
      assertThat(subject.describeBehavior(), equalTo("describesADesiredBehavior"));
    }
    
    @Test
    public void givenAField_returnsTheNameOfTheField() throws Exception {
      Example subject = new FieldExample(getClass().getDeclaredField("somethingINeedToRunMyTest"), null,
        getClass().getDeclaredField("describesADesiredBehavior"));
      assertThat(subject.describeSetup(), equalTo("somethingINeedToRunMyTest"));
    }
  }
  
  public class run {
    public class givenAClassWithoutACallableNoArgConstructor {
      @Test
      public void ThrowsUnsupportedConstructorException() throws Exception  {
        assertThrowsUnsupportedConstructorException(JSpecExamples.HiddenConstructor.class, "is_otherwise_valid");
        assertThrowsUnsupportedConstructorException(JSpecExamples.ConstructorHasArguments.class, "is_otherwise_valid");
      }
      
      private void assertThrowsUnsupportedConstructorException(Class<?> context, String itFieldName) throws Exception {
        Field field = context.getDeclaredField(itFieldName);
        Example subject = new FieldExample(null, null, field);
        assertThrows(UnsupportedConstructorException.class,
          is(String.format("Unable to find a no-argument constructor for class %s", context.getName())),
          NoSuchMethodException.class, subject::run);
      }
    }
    
    public class givenAFaultyConstructorOrInitializer {
      @Test
      public void throwsTestSetupException() throws Exception {
        assertTestSetupException(JSpecExamples.FaultyClassInitializer.class.getDeclaredField("is_otherwise_valid"),
          AssertionError.class);
        assertTestSetupException(JSpecExamples.FaultyConstructor.class.getDeclaredField("is_otherwise_valid"),
          InvocationTargetException.class);
      }
      
      private void assertTestSetupException(Field field, Class<? extends Throwable> expectedCause) {
        Example subject = new FieldExample(null, null, field);
        assertThrows(TestSetupException.class, 
          is(String.format("Failed to create test context %s", field.getDeclaringClass().getName())),
          expectedCause, subject::run);
      }
    }
    
    public class givenAccessibleFields {
      private final List<String> events = new LinkedList<String>();
      private final Example subject;
      
      public givenAccessibleFields() throws Exception {
        this.subject = new FieldExample(
          JSpecExamples.FullFixture.class.getDeclaredField("arranges"),
          JSpecExamples.FullFixture.class.getDeclaredField("acts"), 
          JSpecExamples.FullFixture.class.getDeclaredField("asserts"));
      }
      
      @Before
      public void spy() throws Exception {
        JSpecExamples.FullFixture.setEventListener(events::add);
        subject.run();
      }
      
      @After
      public void releaseSpy() {
        JSpecExamples.FullFixture.setEventListener(null);
      }
      
      @Test
      public void runsTheActionFunctionBeforeTheItFunction() throws Exception {
        assertThat(events, contains(
          "JSpecExamples.FullFixture::new",
          "JSpecExamples.FullFixture::arrange",
          "JSpecExamples.FullFixture::act",
          "JSpecExamples.FullFixture::assert"));
      }
    }
    
    public class whenAFieldCanNotBeAccessed {
      private final Example subject;
      
      public whenAFieldCanNotBeAccessed() throws Exception {
        this.subject = new FieldExample(null, null, HasWrongType.class.getDeclaredField("inaccessibleAsIt"));
      }
      
      @Test
      public void throwsTestSetupExceptionCausedByReflectionError() {
        //Intended to catch ReflectiveOperationException, but causing that with a fake SecurityManager was not reliable
        assertThrows(TestSetupException.class, startsWith("Failed to create test context"), ClassCastException.class,
          subject::run);
      }
    }
    
    public class whenATestFunctionThrows {
      @Test
      public void throwsWhateverEstablishThrows() throws Exception {
        Example subject = new FieldExample(
          JSpecExamples.FailingEstablish.class.getDeclaredField("flawed_setup"), null,
          JSpecExamples.FailingEstablish.class.getDeclaredField("will_never_run"));
        assertThrows(UnsupportedOperationException.class, equalTo("flawed_setup"), subject::run);
      }
      
      @Test
      public void throwsWhateverBecauseThrows() throws Exception {
        Example subject = new FieldExample(
          null, JSpecExamples.FailingBecause.class.getDeclaredField("flawed_action"),
          JSpecExamples.FailingBecause.class.getDeclaredField("will_never_run"));
        assertThrows(UnsupportedOperationException.class, equalTo("flawed_action"), subject::run);
      }
      
      @Test
      public void throwsWhateverItThrows() throws Exception {
        Field field = JSpecExamples.FailingTest.class.getDeclaredField("fails");
        Example subject = new FieldExample(null, null, field);
        assertThrows(AssertionError.class, anything(), subject::run);
      }
    }
  }
  
  public static class HasWrongType {
    public Object inaccessibleAsIt = new Object();
  }
}