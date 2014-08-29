package org.javaspec.runner;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.*;
import static org.javaspec.testutil.Assertions.assertNoThrow;
import static org.javaspec.testutil.Assertions.assertThrows;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import org.javaspec.runner.FieldExample.TestSetupException;
import org.javaspec.runner.FieldExample.UnsupportedConstructorException;
import org.javaspecproto.ContextClasses;
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
    
    public class givenFieldsAccessibleFromAContextClass {
      private final List<String> events = new LinkedList<String>(); //TODO KDK: Don't have to spy; can write the test such that it only passes when everything runs in order
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
      public void instantiatesContextClassesThenRunsSetupThenAssertsThenCleansUp() throws Exception {
        assertThat(events, contains(
          "ContextClasses.FullFixture::new",
          "ContextClasses.FullFixture::arrange",
          "ContextClasses.FullFixture::act",
          "ContextClasses.FullFixture::assert",
          "ContextClasses.FullFixture::cleans"));
      }
    }
    
    public class givenANestedContext {
      @Test
      public void instantiatesTheNestedContextClasses() throws Exception {
        Example subject = exampleWithIt(ContextClasses.NestedThreeDeep.middle.bottom.class, "asserts");
        assertNoThrow(subject::run);
      }
      
      @Test
      public void usesTheTreeOfContextObjectsToRunTheFixtureLambdas() throws Exception {
        Example subject = exampleWithNestedFullFixture();
        assertNoThrow(subject::run);
      }
    }
    
    public class whenATestFunctionThrows {
      @Test
      public void throwsWhateverEstablishOrBecauseThrows() {
        Example subject = exampleWith(ContextClasses.FailingEstablish.class, "will_never_run", 
          newArrayList("flawed_setup"), newArrayList());
        assertThrows(UnsupportedOperationException.class, equalTo("flawed_setup"), subject::run);
      }
      
      @Test
      public void throwsWhateverItThrows() {
        Example subject = exampleWithIt(ContextClasses.FailingIt.class, "fails");
        assertThrows(AssertionError.class, anything(), subject::run);
      }
      
      @Test
      public void throwsWhateverCleanupThrows() {
        Example subject = exampleWith(ContextClasses.FailingCleanup.class, "may_run", 
          newArrayList(), newArrayList("flawed_cleanup"));
        assertThrows(IllegalStateException.class, equalTo("flawed_cleanup"), subject::run);
      }
    }
    
    public class givenACleanupField {
      public class whenASetupActionOrAssertionFunctionThrows {
        private final List<String> events = new LinkedList<String>();
        private final Example subject = exampleWith(ContextClasses.FailingEstablishWithCleanup.class, "it",
          newArrayList("establish"), newArrayList("cleanup"));
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
  
  private static Example exampleWithFullFixture() {
    return exampleWith(ContextClasses.FullFixture.class, "asserts", 
      newArrayList("arranges", "acts"), newArrayList("cleans"));
  }
  
  private static Example exampleWithIt(Class<?> context, String name) {
    return exampleWith(context, name, newArrayList(), newArrayList());
  }
  
  private static Example exampleWithNestedFullFixture() {
    return new FieldExample(ContextClasses.NestedFullFixture.class.getSimpleName(),
      readField(ContextClasses.NestedFullFixture.innerContext.class, "asserts"),
      newArrayList(readField(ContextClasses.NestedFullFixture.class, "arranges"),
        readField(ContextClasses.NestedFullFixture.innerContext.class, "acts")),
      newArrayList(readField(ContextClasses.NestedFullFixture.class, "cleans")));
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