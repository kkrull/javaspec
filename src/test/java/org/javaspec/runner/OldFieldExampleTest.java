package org.javaspec.runner;

import static org.hamcrest.Matchers.*;
import static org.javaspec.testutil.Assertions.assertThrows;
import static org.junit.Assert.assertThat;

import java.util.LinkedList;
import java.util.List;

import org.javaspec.proto.ContextClasses;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class OldFieldExampleTest { 
  public class run {
    public class whenATestFunctionThrows {
      @Test
      public void throwsWhateverEstablishThrows() {
        IOldExample subject = exampleWithEstablish(ContextClasses.FailingEstablish.class, "flawed_setup", "will_never_run");
        assertThrows(UnsupportedOperationException.class, equalTo("flawed_setup"), subject::run);
      }
      
      @Test
      public void throwsWhateverBecauseThrows() {
        IOldExample subject = exampleWithBecause(ContextClasses.FailingBecause.class, "flawed_action", "will_never_run");
        assertThrows(UnsupportedOperationException.class, equalTo("flawed_action"), subject::run);
      }
      
      @Test
      public void throwsWhateverItThrows() {
        IOldExample subject = exampleWithIt(ContextClasses.FailingIt.class, "fails");
        assertThrows(AssertionError.class, anything(), subject::run);
      }
      
      @Test
      public void throwsWhateverCleanupThrows() {
        IOldExample subject = exampleWithCleanup(ContextClasses.FailingCleanup.class, "may_run", "flawed_cleanup");
        assertThrows(IllegalStateException.class, equalTo("flawed_cleanup"), subject::run);
      }
    }

    public class givenACleanupField {
      public class whenASetupActionOrAssertionFunctionThrows {
        private final List<String> events = new LinkedList<String>();
        private final IOldExample subject = exampleWith(ContextClasses.FailingEstablishWithCleanup.class,
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
  
  public class whenAccessingFieldsMultipleTimes {
    private final IOldExample subject = exampleWithIt(ContextClasses.UnstableConstructor.class, "asserts");
    
    @Test
    public void onlyConstructsTheClassOnce() throws Exception {
      //General precautions; it could get confusing if a context class manages to change state between isSkipped and run
      subject.isSkipped();
      subject.run();
    }
  }
  
  private static IOldExample exampleWithEstablish(Class<?> context, String establishField, String itField) {
    return exampleWith(context, establishField, null, itField, null);
  }
  
  private static IOldExample exampleWithBecause(Class<?> context, String becauseField, String itField) {
    return exampleWith(context, null, becauseField, itField, null);
  }
  
  private static IOldExample exampleWithIt(Class<?> context, String name) {
    return exampleWith(context, null, null, name, null);
  }
  
  private static IOldExample exampleWithCleanup(Class<?> context, String itField, String cleanupField) {
    return exampleWith(context, null, null, itField, cleanupField);
  }
  
  private static IOldExample exampleWith(Class<?> context, String establish, String because, String it, String cleanup) {
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
  
  public static class HasWrongType {
    public Object inaccessibleAsIt = new Object();
  }
}