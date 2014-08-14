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
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class FieldExampleTest {
  @Test
  public void givenAnEstablishField_describesAndRunsIt() {
    Assert.fail("pending");
  }
  
  public class describeBehavior {
    It describesADesiredBehavior;
    
    @Test
    public void returnsTheNameOfTheField() throws Exception {
      Example subject = new FieldExample(null, getClass().getDeclaredField("describesADesiredBehavior"));
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
        Example subject = new FieldExample(null, field);
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
        Example subject = new FieldExample(null, field);
        assertThrows(TestSetupException.class, 
          is(String.format("Failed to construct test context %s", field.getDeclaringClass().getName())),
          expectedCause, subject::run);
      }
    }
    
    public class givenAnAccessibleItField {
      private final Example subject;
      
      public givenAnAccessibleItField() throws Exception {
        Field field = JSpecExamples.One.class.getDeclaredField("only_test");
        this.subject = new FieldExample(null, field);
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
        this.subject = new FieldExample(null, field);
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
      @Test
      public void throwsWhateverTheExampleThrew() throws Exception {
        Field field = JSpecExamples.FailingTest.class.getDeclaredField("fails");
        Example subject = new FieldExample(null, field);
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