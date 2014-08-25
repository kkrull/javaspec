package org.javaspec.runner;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.*;
import static org.javaspec.testutil.Assertions.assertThrows;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Collections;

import org.javaspec.proto.ContextClasses;
import org.javaspec.proto.OuterContext;
import org.javaspec.runner.ClassExampleGateway.NoExamplesException;
import org.javaspec.runner.ClassExampleGateway.UnknownStepExecutionSequenceException;
import org.javaspec.testutil.Assertions;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class ClassExampleGatewayTest {
  public class findInitializationErrors {
    public class givenAClassWith0OrMoreInnerClasses {
      @Test
      public void givenAClassWith2OrMoreEstablishFields_containsUnknownStepExecutionSequenceException() {
        shouldFindInitializationError(ContextClasses.TwoEstablish.class, UnknownStepExecutionSequenceException.class,
          "Impossible to determine running order of multiple Establish functions in test context org.javaspec.proto.ContextClasses$TwoEstablish");
      }
      
      @Test
      public void givenAClassWith2OrMoreBecauseFields_containsUnknownStepExecutionSequenceException() {
        shouldFindInitializationError(ContextClasses.TwoBecause.class, UnknownStepExecutionSequenceException.class,
          "Impossible to determine running order of multiple Because functions in test context org.javaspec.proto.ContextClasses$TwoBecause");
      }
      
      @Test
      public void givenAClassWith2OrMoreCleanupFields_containsUnknownStepExecutionSequenceException() {
        shouldFindInitializationError(ContextClasses.TwoCleanup.class, UnknownStepExecutionSequenceException.class,
          "Impossible to determine running order of multiple Cleanup functions in test context org.javaspec.proto.ContextClasses$TwoCleanup");
      }
      
      @Test
      public void givenNoClassesWith1OrMoreItFields_containsNoExamplesException() {
        shouldFindInitializationError(ContextClasses.Empty.class, NoExamplesException.class,
          "Test context org.javaspec.proto.ContextClasses$Empty must contain at least 1 example in an It field");
      }
      
      class givenAtLeast1ItFieldSomewhere_andNoClassesWith2OrMoreOfTheSameFixture {
        @Test
        public void returnsEmptyList() {
          ExampleGateway subject = new ClassExampleGateway(ContextClasses.NestedThreeDeep.class);
          assertThat(subject.findInitializationErrors(), equalTo(Collections.emptyList()));
        }
      }
    }
    
    private void shouldFindInitializationError(Class<?> contextType, Class<?> errorType, String errorMsg) {
      ExampleGateway subject = new ClassExampleGateway(contextType);
      assertThat(subject.findInitializationErrors().stream().map(x -> x.getClass()).collect(toList()),
        contains(equalTo(errorType)));
      assertThat(subject.findInitializationErrors().stream().map(Throwable::getMessage).collect(toList()),
        contains(equalTo(errorMsg)));
    }
  }
  
  public class getRootContext {
    public class givenAClassContaining {
      public class anyLevelOfNestedInnerClasses {
        @Test
        public void returnsAContextNodeForEachInnerClassSubtreeContaining1OrMoreItFields() {
          ExampleGateway subject = new ClassExampleGateway(ContextClasses.Nested3By2.class);
          Context rootContext = subject.getRootContext();
          assertThat(rootContext.name, equalTo("Nested3By2"));
          assertThat(rootContext.getSubContexts(), hasSize(2));
          
          assert2By1Context(rootContext.getSubContexts().get(0), "level2a", "level3a");
          assert2By1Context(rootContext.getSubContexts().get(1), "level2b", "level3b");
        }
        
        @Test
        public void skipsStaticNestedClasses() {
          ExampleGateway subject = new ClassExampleGateway(ContextClasses.NestedWithStaticHelperClass.class);
          Context rootContext = subject.getRootContext();
          assert2By1Context(rootContext, "NestedWithStaticHelperClass", "context");
        }
        
        @Test
        public void skipsInnerClassSubtreesThatContainNoItFields() {
          ExampleGateway subject = new ClassExampleGateway(ContextClasses.NestedWithInnerHelperClass.class);
          Context rootContext = subject.getRootContext();
          assert2By1Context(rootContext, "NestedWithInnerHelperClass", "context");
        }
        
        private void assert2By1Context(Context context, String parentName, String childName) {
          assertThat(context.name, equalTo(parentName));
          assertThat(context.getSubContexts().stream().map(x -> x.name).collect(toList()), contains(childName));
        }
      }
    }
  }
}