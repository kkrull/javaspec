package org.jspec.runner;

import static org.hamcrest.Matchers.*;
import static org.jspec.util.Assertions.assertThrows;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ReflectPermission;
import java.security.AccessControlException;
import java.security.Permission;
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
    protected final List<String> events = new LinkedList<String>();
    
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
          is(String.format("Failed to construct test context %s", field.getDeclaringClass().getName())),
          expectedCause, subject::run);
      }
    }
    
    public class givenAccessibleFields {
      private Field establishField;
      private Field establishedItField;
      private Field itField;
      
      public givenAccessibleFields() throws Exception {
        this.establishField = JSpecExamples.EstablishOnce.class.getDeclaredField("that");
        this.establishedItField = JSpecExamples.EstablishOnce.class.getDeclaredField("runs");
        this.itField = JSpecExamples.One.class.getDeclaredField("only_test");
      }
      
      @Before
      public void spy() throws Exception {
        JSpecExamples.One.setEventListener(events::add);
        JSpecExamples.EstablishOnce.setEventListener(events::add);
      }
      
      @After
      public void releaseSpy() {
        JSpecExamples.One.setEventListener(null);
        JSpecExamples.EstablishOnce.setEventListener(null);
      }
      
      @Test
      public void constructsTheContextClassThenRunsTheFunctionAssignedToTheItField() throws Exception {
        Example subject = new FieldExample(null, null, itField);
        subject.run();
        assertThat(events, contains("JSpecExamples.One::new", "JSpecExamples.One::only_test"));
      }
      
      @Test
      public void runsTheSetupFunctionBeforeTheItFunction() throws Exception {
        Example subject = new FieldExample(establishField, null, establishedItField);
        subject.run();
        assertThat(events, contains("JSpecExamples.EstablishOnce::that", "JSpecExamples.EstablishOnce::runs"));
      }
      
      @Test
      public void runsTheActionFunctionBeforeTheItFunction() throws Exception {
        fail("pending");
      }
    }
    
    public class whenAFieldCanNotBeAccessed {
      private SecurityManager originalManager;
      private final Field establishField;
      private final Field itField;
      
      public whenAFieldCanNotBeAccessed() throws Exception {
        //Access the fields *before* putting in the restricted security manager
        this.establishField = JSpecExamples.EstablishOnce.class.getDeclaredField("that");
        this.itField = JSpecExamples.One.class.getDeclaredField("only_test");
      }
      
      @Before
      public void stubSecurityManager() {
        this.originalManager = System.getSecurityManager();
        System.setSecurityManager(new NoReflectionForYouSecurityManager());
      }
      
      @After
      public void unstub() {
        System.setSecurityManager(originalManager);
      }
      
      @Test
      public void accessingEstablishField_throwsTestSetupExceptionCausedByReflectionError() {
        //No way for security manager to deny access to fields selectively => will fail if subject reflects on It first 
        Example subject = new FieldExample(establishField, null, itField);
        assertThrows(TestSetupException.class,
          is("Failed to access test function org.jspec.proto.JSpecExamples$EstablishOnce.that"),
          AccessControlException.class,
          subject::run);
      }
      
      @Test
      public void accessingItField_throwsTestSetupExceptionCausedByReflectionError() {
        Example subject = new FieldExample(null, null, itField);
        assertThrows(TestSetupException.class,
          is("Failed to access test function org.jspec.proto.JSpecExamples$One.only_test"),
          AccessControlException.class,
          subject::run);
      }
    }
    
    public class whenAnEstablishFunctionThrows {
      @Test
      public void throwsWhateverTheFunctionThrew() throws Exception {
        Example subject = new FieldExample(
          JSpecExamples.FailingEstablish.class.getDeclaredField("flawed_setup"), null,
          JSpecExamples.FailingEstablish.class.getDeclaredField("will_never_run"));
        assertThrows(UnsupportedOperationException.class, equalTo("flawed_setup"), subject::run);
      }
    }
    
    public class whenAnItFunctionThrows {
      @Test
      public void throwsWhateverTheFunctionThrew() throws Exception {
        Field field = JSpecExamples.FailingTest.class.getDeclaredField("fails");
        Example subject = new FieldExample(null, null, field);
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