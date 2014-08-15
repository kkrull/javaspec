package org.jspec.runner;

import static org.hamcrest.Matchers.*;
import static org.jspec.util.Assertions.assertThrows;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

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
  public class descriptionMethods {
    public class givenNoFixtureFields {
      private final Example subject;
      
      public givenNoFixtureFields() throws Exception {
        this.subject = new FieldExample(null, null, 
          JSpecExamples.OneIt.class.getDeclaredField("only_test"),
          null);
      }
      
      @Test
      public void fixtureMethodDescriptors_returnBlank() {
        assertThat(subject.describeSetup(), equalTo(""));
        assertThat(subject.describeAction(), equalTo(""));
        assertThat(subject.describeCleanup(), equalTo(""));
      }
    }
    
    public class givenAValueForAField {
      private final Example subject;
      
      public givenAValueForAField() throws Exception {
        this.subject = new FieldExample(
          JSpecExamples.FullFixture.class.getDeclaredField("arranges"),
          JSpecExamples.FullFixture.class.getDeclaredField("acts"), 
          JSpecExamples.FullFixture.class.getDeclaredField("asserts"),
          JSpecExamples.FullFixture.class.getDeclaredField("cleans"));
      }
      
      @Test
      public void returnsTheNameOfTheField() {
        assertThat(subject.describeSetup(), equalTo("arranges"));
        assertThat(subject.describeAction(), equalTo("acts"));
        assertThat(subject.describeBehavior(), equalTo("asserts"));
        assertThat(subject.describeCleanup(), equalTo("cleans"));
      }
    }
  }
  
  public class run {
    public class givenAClassWithoutACallableNoArgConstructor {
      @Test
      public void ThrowsUnsupportedConstructorException() throws Exception  {
        assertThrowsUnsupportedConstructorException(JSpecExamples.ConstructorHidden.class, "is_otherwise_valid");
        assertThrowsUnsupportedConstructorException(JSpecExamples.ConstructorWithArguments.class, "is_otherwise_valid");
      }
      
      private void assertThrowsUnsupportedConstructorException(Class<?> context, String itFieldName) throws Exception {
        Field field = context.getDeclaredField(itFieldName);
        Example subject = new FieldExample(null, null, field, null);
        assertThrows(UnsupportedConstructorException.class,
          is(String.format("Unable to find a no-argument constructor for class %s", context.getName())),
          NoSuchMethodException.class, subject::run);
      }
    }
    
    public class givenAFaultyConstructorOrInitializer {
      @Test
      public void throwsTestSetupException() throws Exception {
        assertTestSetupException(JSpecExamples.FailingClassInitializer.class.getDeclaredField("is_otherwise_valid"),
          AssertionError.class);
        assertTestSetupException(JSpecExamples.FailingConstructor.class.getDeclaredField("is_otherwise_valid"),
          InvocationTargetException.class);
      }
      
      private void assertTestSetupException(Field field, Class<? extends Throwable> expectedCause) {
        Example subject = new FieldExample(null, null, field, null);
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
          JSpecExamples.FullFixture.class.getDeclaredField("asserts"),
          null);
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
        this.subject = new FieldExample(null, null, HasWrongType.class.getDeclaredField("inaccessibleAsIt"), null);
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
          JSpecExamples.FailingEstablish.class.getDeclaredField("flawed_setup"),
          null,
          JSpecExamples.FailingEstablish.class.getDeclaredField("will_never_run"),
          null);
        assertThrows(UnsupportedOperationException.class, equalTo("flawed_setup"), subject::run);
      }
      
      @Test
      public void throwsWhateverBecauseThrows() throws Exception {
        Example subject = new FieldExample(
          null,
          JSpecExamples.FailingBecause.class.getDeclaredField("flawed_action"),
          JSpecExamples.FailingBecause.class.getDeclaredField("will_never_run"),
          null);
        assertThrows(UnsupportedOperationException.class, equalTo("flawed_action"), subject::run);
      }
      
      @Test
      public void throwsWhateverItThrows() throws Exception {
        Field field = JSpecExamples.FailingIt.class.getDeclaredField("fails");
        Example subject = new FieldExample(null, null, field, null);
        assertThrows(AssertionError.class, anything(), subject::run);
      }
      
      @Test
      public void throwsWhateverCleanupThrows() throws Exception {
        Example subject = new FieldExample(
          null,
          null,
          JSpecExamples.FailingCleanup.class.getDeclaredField("may_run"),
          JSpecExamples.FailingCleanup.class.getDeclaredField("flawed_cleanup"));
        assertThrows(IllegalStateException.class, equalTo("flawed_cleanup"), subject::run);
      }
    }

    public class givenACleanupField {
      public class whenASetupActionOrAssertionFunctionThrows {
        private final List<String> events = new LinkedList<String>();
        private final Example subject;
        private Throwable thrown;
        
        public whenASetupActionOrAssertionFunctionThrows() throws Exception {
          this.subject = new FieldExample(
            JSpecExamples.FailingEstablishWithCleanup.class.getDeclaredField("establish"),
            null,
            JSpecExamples.FailingEstablishWithCleanup.class.getDeclaredField("it"), 
            JSpecExamples.FailingEstablishWithCleanup.class.getDeclaredField("cleanup"));
        }
        
        @Before
        public void spy() throws Exception {
          JSpecExamples.FailingEstablishWithCleanup.setEventListener(events::add);
          try {
            subject.run();
          } catch (Throwable t) {
            this.thrown = t;
          }
        }
        
        @After
        public void releaseSpy() {
          JSpecExamples.FailingEstablishWithCleanup.setEventListener(null);
        }
        
        @Test
        public void runsTheCleanupInCaseAnyStatementsExecutedBeforeTheError() {
          assertThat(events, contains(
            "JSpecExamples.FailingEstablishWithCleanup::establish",
            "JSpecExamples.FailingEstablishWithCleanup::cleanup"));
        }
        
        @Test
        public void stillThrowsTheOriginalException() {
          assertThat(thrown.getMessage(), equalTo("flawed_setup"));
        }
      }
    }
  }
  
  public static class HasWrongType {
    public Object inaccessibleAsIt = new Object();
  }
}