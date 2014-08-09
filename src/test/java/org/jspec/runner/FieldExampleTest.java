package org.jspec.runner;

import static org.hamcrest.Matchers.*;
import static org.jspec.util.Assertions.assertThrows;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ReflectPermission;
import java.security.AccessControlException;
import java.security.Permission;
import java.util.LinkedList;
import java.util.List;

import org.jspec.dsl.It;
import org.jspec.proto.JSpecExamples;
import org.jspec.runner.FieldExample.TestRunException;
import org.jspec.runner.FieldExample.TestSetupException;
import org.jspec.runner.FieldExample.UnsupportedConstructorException;
import org.jspec.runner.FieldExample.UnsupportedFieldException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class FieldExampleTest {
  public class constructor {
    public class givenABehaviorFieldOfSomeOtherType {
      private final Class<?> contextClass = JSpecExamples.WrongTypeOfBehaviorField.class;
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
          NoSuchMethodException.class, subject::run);
      }
    }
    
    public class givenAFaultyConstructorOrInitializer {
      @Test
      public void throwsTestSetupExceptionCausedByAFaultyConstructor() throws Exception {
        assertTestSetupException(JSpecExamples.FaultyConstructor.class.getDeclaredField("is_otherwise_valid"),
          InvocationTargetException.class);
      }
      
      @Test
      public void throwsTestSetupExceptionCausedByAFaultyInitializer() throws Exception {
        assertTestSetupException(JSpecExamples.FaultyClassInitializer.class.getDeclaredField("is_otherwise_valid"),
          AssertionError.class);
      }
      
      private void assertTestSetupException(Field field, Class<? extends Throwable> expectedCause) {
        Example subject = new FieldExample(field);
        assertThrows(TestSetupException.class, 
          is(String.format("Failed to construct test context %s", field.getDeclaringClass().getName())),
          expectedCause, subject::run);
      }
    }
    
    public class givenAnAccessibleItField {
      private final Example subject;
      
      public givenAnAccessibleItField() throws Exception {
        Field field = JSpecExamples.One.class.getDeclaredField("only_test");
        this.subject = new FieldExample(field);
      }
      
      @Before
      public void spy() throws Exception {
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
    
    public class whenAnItFieldCanNotBeAccessed {
      private final Example subject;
      SecurityManager originalManager;
      
      public whenAnItFieldCanNotBeAccessed() throws Exception {
        Field field = JSpecExamples.One.class.getDeclaredField("only_test");
        this.subject = new FieldExample(field);
      }
      
      @Before
      public void stubSecurityManager() {
        this.originalManager = System.getSecurityManager();
        System.setSecurityManager(new NoReflectionForYouSecurityManager());
      }
      
      @After
      public void unstub() {
        System.setSecurityManager(null);
      }
      
      @Test
      public void throwsTestSetupExceptionCausedByTheConstructorInvocation() {
        assertThrows(TestRunException.class,
          is("Failed to access example behavior defined by org.jspec.proto.JSpecExamples$One.only_test"),
          AccessControlException.class,
          subject::run);
      }
    }
    
    public class whenABehaviorThrows {
      private final Example subject;
      
      public whenABehaviorThrows() throws Exception {
        Field field = JSpecExamples.FailingTest.class.getDeclaredField("fails");
        this.subject = new FieldExample(field);
      }
      
      @Test
      public void throwsWhateverTheExampleThrew() {
        assertThrows(AssertionError.class, anything(), subject::run);
      }
    }
  }
  
  static class NoReflectionForYouSecurityManager extends SecurityManager {
    @Override
    public void checkPermission(Permission perm) {
      if(perm instanceof ReflectPermission)
        throw new AccessControlException("No reflection for you!!!", perm);
    }
  }
}