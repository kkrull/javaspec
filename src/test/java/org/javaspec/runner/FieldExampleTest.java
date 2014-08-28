package org.javaspec.runner;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.*;
import static org.javaspec.testutil.Assertions.assertThrows;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import org.javaspec.proto.ContextClasses;
import org.javaspec.runner.FieldExample.TestSetupException;
import org.javaspec.runner.FieldExample.UnsupportedConstructorException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class FieldExampleTest {
  public class getName {
    @Test
    public void returnsTheNameOfTheGivenItField() {
      Example subject = exampleWithIt(ContextClasses.PendingIt.class, "asserts");
      assertThat(subject.getName(), equalTo("asserts"));
    }
  }
  
  public class isSkipped {
    public class whenEachJavaSpecFieldHasAnAssignedValue {
      @Test
      public void returnsFalse() {
        shouldBeSkipped(ContextClasses.FullFixture.class, false);
      }
    }
    
    public class when1OrMoreJavaSpecFieldsDoNotHaveAnAssignedValue {
      @Test
      public void returnsTrue() {
        shouldBeSkipped(ContextClasses.PendingEstablish.class, true);
        shouldBeSkipped(ContextClasses.PendingBecause.class, true);
        shouldBeSkipped(ContextClasses.PendingIt.class, true);
        
        Example pendingCleanup = exampleWith(ContextClasses.PendingCleanup.class, "asserts", 
          newArrayList("arranges", "acts"), newArrayList("cleans"));
        assertThat(pendingCleanup.isSkipped(), equalTo(true));
      }
    }
    
    private void shouldBeSkipped(Class<?> contextClass, boolean isSkipped) {
      Example subject = exampleWith(contextClass, "asserts", newArrayList("arranges", "acts"), newArrayList());
      assertThat(subject.isSkipped(), equalTo(isSkipped));
    }
  }
  
  public class run {
    public class givenAClassWithoutACallableNoArgConstructor {
      @Test
      public void ThrowsUnsupportedConstructorException() {
        assertThrowsUnsupportedConstructorException(ContextClasses.ConstructorHidden.class, "is_otherwise_valid");
        assertThrowsUnsupportedConstructorException(ContextClasses.ConstructorWithArguments.class, "is_otherwise_valid");
      }
      
      private void assertThrowsUnsupportedConstructorException(Class<?> context, String itFieldName) {
        Example subject = exampleWithIt(context, itFieldName);
        assertThrows(UnsupportedConstructorException.class,
          is(String.format("Unable to find a no-argument constructor for class %s", context.getName())),
          NoSuchMethodException.class, subject::run);
      }
    }
    
    public class givenAFaultyConstructorOrInitializer {
      @Test
      public void throwsTestSetupException() throws Exception {
        assertTestSetupException(ContextClasses.FailingClassInitializer.class, "will_fail", AssertionError.class);
        assertTestSetupException(ContextClasses.FailingConstructor.class, "will_fail", InvocationTargetException.class);
      }
      
      private void assertTestSetupException(Class<?> context, String itFieldName, Class<? extends Throwable> cause) {
        Example subject = exampleWithIt(context, itFieldName);
        assertThrows(TestSetupException.class, 
          is(String.format("Failed to create test context %s", context.getName())),
          cause, subject::run);
      }
    }
    
    public class whenAFieldCanNotBeAccessed {
      @Test
      public void throwsTestSetupExceptionCausedByReflectionError() {
        //Intended to catch ReflectiveOperationException, but causing that with a fake SecurityManager was not reliable
        Example subject = exampleWithIt(HasWrongType.class, "inaccessibleAsIt");
        assertThrows(TestSetupException.class, startsWith("Failed to create test context"), ClassCastException.class,
          subject::run);
      }
    }
    
    public class givenAccessibleFields {
      private final List<String> events = new LinkedList<String>();
      private final Example subject = exampleWithFullFixture();
      
      @Before
      public void spy() throws Exception {
        ContextClasses.FullFixture.setEventListener(events::add);
        subject.run();
      }
      
      @After
      public void releaseSpy() {
        ContextClasses.FullFixture.setEventListener(null);
      }
      
      @Test
      public void runsTheActionFunctionBeforeTheItFunction() throws Exception {
        assertThat(events, contains(
          "ContextClasses.FullFixture::new",
          "ContextClasses.FullFixture::arrange",
          "ContextClasses.FullFixture::act",
          "ContextClasses.FullFixture::assert",
          "ContextClasses.FullFixture::cleans"));
      }
    }
  }
  
  public class _run {
    public class whenATestFunctionThrows {
      @Test
      public void throwsWhateverEstablishThrows() {
        IOldExample subject = _exampleWithEstablish(ContextClasses.FailingEstablish.class, "flawed_setup", "will_never_run");
        assertThrows(UnsupportedOperationException.class, equalTo("flawed_setup"), subject::run);
      }
      
      @Test
      public void throwsWhateverBecauseThrows() {
        IOldExample subject = _exampleWithBecause(ContextClasses.FailingBecause.class, "flawed_action", "will_never_run");
        assertThrows(UnsupportedOperationException.class, equalTo("flawed_action"), subject::run);
      }
      
      @Test
      public void throwsWhateverItThrows() {
        IOldExample subject = _exampleWithIt(ContextClasses.FailingIt.class, "fails");
        assertThrows(AssertionError.class, anything(), subject::run);
      }
      
      @Test
      public void throwsWhateverCleanupThrows() {
        IOldExample subject = _exampleWithCleanup(ContextClasses.FailingCleanup.class, "may_run", "flawed_cleanup");
        assertThrows(IllegalStateException.class, equalTo("flawed_cleanup"), subject::run);
      }
    }

    public class givenACleanupField {
      public class whenASetupActionOrAssertionFunctionThrows {
        private final List<String> events = new LinkedList<String>();
        private final IOldExample subject = _exampleWith(ContextClasses.FailingEstablishWithCleanup.class,
          "establish", null, "it", "cleanup");
        private Throwable thrown;
        
        @Before
        public void spy() throws Exception {
          ContextClasses.FailingEstablishWithCleanup.setEventListener(events::add);
          try {
            subject.run();
          } catch (Throwable t) {
            this.thrown = t;
          }
        }
        
        @After
        public void releaseSpy() {
          ContextClasses.FailingEstablishWithCleanup.setEventListener(null);
        }
        
        @Test
        public void runsTheCleanupInCaseAnyStatementsExecutedBeforeTheError() {
          assertThat(events, contains(
            "ContextClasses.FailingEstablishWithCleanup::establish",
            "ContextClasses.FailingEstablishWithCleanup::cleanup"));
        }
        
        @Test
        public void stillThrowsTheOriginalException() {
          assertThat(thrown.getMessage(), equalTo("flawed_setup"));
        }
      }
    }
  }
  
  public class _whenAccessingFieldsMultipleTimes {
    private final IOldExample subject = _exampleWithIt(ContextClasses.UnstableConstructor.class, "asserts");
    
    @Test
    public void onlyConstructsTheClassOnce() throws Exception {
      //General precautions; it could get confusing if a context class manages to change state between isSkipped and run
      subject.isSkipped();
      subject.run();
    }
  }
  
  private static IOldExample _exampleWithEstablish(Class<?> context, String establishField, String itField) {
    return _exampleWith(context, establishField, null, itField, null);
  }
  
  private static IOldExample _exampleWithBecause(Class<?> context, String becauseField, String itField) {
    return _exampleWith(context, null, becauseField, itField, null);
  }
  
  private static IOldExample _exampleWithIt(Class<?> context, String name) {
    return _exampleWith(context, null, null, name, null);
  }
  
  private static IOldExample _exampleWithCleanup(Class<?> context, String itField, String cleanupField) {
    return _exampleWith(context, null, null, itField, cleanupField);
  }
  
  private static IOldExample _exampleWith(Class<?> context, String establish, String because, String it, String cleanup) {
    try {
      return new OldFieldExample(
        establish == null ? null : context.getDeclaredField(establish),
          because == null ? null : context.getDeclaredField(because),
            it == null ? null : context.getDeclaredField(it),
              cleanup == null ? null : context.getDeclaredField(cleanup));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
  
  private static Example exampleWithFullFixture() {
    return exampleWith(ContextClasses.FullFixture.class, "asserts", newArrayList("arranges", "acts"), newArrayList("cleans"));
  }
  
  private static Example exampleWithIt(Class<?> context, String name) {
    return exampleWith(context, name, newArrayList(), newArrayList());
  }
  
  private static Example exampleWith(Class<?> context, String it, List<String> befores, List<String> afters) {
    try {
      return new FieldExample(context.getSimpleName(), 
        it == null ? null : context.getDeclaredField(it),
        befores.stream().map(x -> readField(context, x)).collect(toList()),
        afters.stream().map(x -> readField(context, x)).collect(toList()));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
  
  private static Field readField(Class<?> context, String name) {
    try {
      return context.getDeclaredField(name);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
  
  public static class HasWrongType {
    public Object inaccessibleAsIt = new Object();
  }
}