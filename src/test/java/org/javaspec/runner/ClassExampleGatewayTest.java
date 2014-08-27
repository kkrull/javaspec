package org.javaspec.runner;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.*;
import static org.javaspec.testutil.Assertions.assertListEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.javaspec.proto.ContextClasses;
import org.javaspec.proto.ContextClasses.NestedWithStaticHelperClass;
import org.javaspec.runner.ClassExampleGateway.UnknownStepExecutionSequenceException;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableList;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class ClassExampleGatewayTest {
  public class findInitializationErrors {
    public class givenAnyTreeOfInnerClasses {
      @Test
      public void andAClassWith2OrMoreEstablishFields_containsUnknownStepExecutionSequenceException() {
        shouldFindInitializationError(ContextClasses.TwoEstablish.class, UnknownStepExecutionSequenceException.class,
          "Impossible to determine running order of multiple Establish functions in test context org.javaspec.proto.ContextClasses$TwoEstablish");
      }
      
      @Test
      public void andAClassWith2OrMoreBecauseFields_containsUnknownStepExecutionSequenceException() {
        shouldFindInitializationError(ContextClasses.TwoBecause.class, UnknownStepExecutionSequenceException.class,
          "Impossible to determine running order of multiple Because functions in test context org.javaspec.proto.ContextClasses$TwoBecause");
      }
      
      @Test
      public void andAClassWith2OrMoreCleanupFields_containsUnknownStepExecutionSequenceException() {
        shouldFindInitializationError(ContextClasses.TwoCleanup.class, UnknownStepExecutionSequenceException.class,
          "Impossible to determine running order of multiple Cleanup functions in test context org.javaspec.proto.ContextClasses$TwoCleanup");
      }
      
      class andAtLeast1ItFieldSomewhere_andNoClassesWith2OrMoreOfTheSameFixture {
        @Test
        public void returnsEmptyList() {
          ExampleGateway subject = new ClassExampleGateway(ContextClasses.NestedThreeDeep.class);
          assertThat(subject.findInitializationErrors(), equalTo(Collections.emptyList()));
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
  }
  
  public class getExamples {
    public class givenAClassWith1OrMoreNestedStaticClasses {
      @Test
      public void doesNotCreateExamplesForItFieldsDeclaredInAStaticNestedClass() {
        assertThat(extractNames(readExamples(NestedWithStaticHelperClass.class)), contains("asserts"));
      }
    }
    
    public class givenAClassWith0OrMoreInnerClasses {
      public class andThereAreNoItFieldsInTheTreeOfTheClassAndItsInnerClasses {
        @Test
        public void returnsNoExamples() {
          assertThat(readExamples(ContextClasses.Empty.class), empty());
        }
      }
      
      public class andAtLeast1ItFieldExistsSomewhereInTheTreeOfThisClassAndItsInnerClasses {
        @Test
        public void returnsAnExampleForEachItField() {
          assertThat(extractNames(readExamples(ContextClasses.TwoIt.class)), contains("first_test", "second_test"));
          assertThat(extractNames(readExamples(ContextClasses.NestedExamples.class)),
            contains("top_level_test", "bottom_test", "middle_test", "another_bottom_test"));
        }
        
        @Test
        public void usesTheContainingClassNameAsTheContextName() {
          assertThat(extractContextNames(readExamples(ContextClasses.OneIt.class)), contains("OneIt"));
        }
        
        @Test @Ignore
        public void includesFixtureFunctionsForEstablishFieldsInTheContextScope() {
          fail("pending");
        }
        
        @Test @Ignore
        public void includesFixtureFunctionsForBecauseFieldsInTheContextScope() {
          fail("pending");
        }
        
        @Test @Ignore
        public void ordersEstablishBeforeBecauseInEachContext() {
          fail("pending");
        }
        
        @Test @Ignore
        public void includesFixtureFunctionsForCleanupFieldsInTheContextScope() {
          fail("pending");
        }
      }
    }
    
    private List<String> extractContextNames(List<NewExample> examples) {
      return examples.stream().map(NewExample::getContextName).collect(toList());
    }
    
    private List<String> extractNames(List<NewExample> examples) {
      return examples.stream().map(NewExample::getName).collect(toList());
    }
    
    private List<NewExample> readExamples(Class<?> context) {
      ExampleGateway subject = new ClassExampleGateway(context);
      return subject.getExamples().collect(toList());
    }
  }
  
  public class getRootContext {
    @Test
    public void returnsAContextNamedAfterTheClass() {
      Context rootContext = doGetRootContext(ContextClasses.OneIt.class);
      assertThat(rootContext.id, equalTo(ContextClasses.OneIt.class));
      assertThat(rootContext.name, equalTo("OneIt"));
    }
    
    @Test
    public void givenAClassDeclaringNoItFields_returnsAContextWithoutExamples() {
      assertExampleNames(doGetRootContext(ContextClasses.Empty.class));
    }
    
    @Test
    public void givenAClassDeclaring1OrMoreItFields_returnsAContextWithThoseExampleNames() {
      assertExampleNames(doGetRootContext(ContextClasses.TwoIt.class), "first_test", "second_test");
    }
    
    private Context doGetRootContext(Class<?> contextClass) {
      ExampleGateway subject = new ClassExampleGateway(contextClass);
      return subject.getRootContext();
    }
  }
  
  public class getRootContextName {
    @Test
    public void returnsTheNameOfTheGivenClass() {
      ExampleGateway subject = new ClassExampleGateway(ContextClasses.OneIt.class);
      assertThat(subject.getRootContextName(), equalTo("OneIt"));
    }
  }
  
  public class getSubContexts {
    public class givenAClassThatHasNoNestedClasses {
      @Test
      public void returnsEmpty() {
        assertContextClasses(subContexts(ContextClasses.Empty.class), ImmutableList.of());
      }
    }
    
    public class givenAClassWith1OrMoreNestedClasses {
      public class whenANestedClassIsStatic {
        @Test
        public void doesNotMakeACorrespondingSubcontext() {
          assertContextClasses(subContexts(ContextClasses.NestedWithStaticHelperClass.class),
            ImmutableList.of(NestedWithStaticHelperClass.context.class));
        }
      }
      
      public class whenANestedInnerClassHasNoItFieldsInItsSubtree {
        @Test
        public void doesNotMakeACorrespondingSubcontext() {
          assertContextClasses(subContexts(ContextClasses.NestedWithInnerHelperClass.class),
            ImmutableList.of(ContextClasses.NestedWithInnerHelperClass.context.class));
        }
      }
      
      public class whenANestedClassIsAnInnerClassWithAnItFieldSomewhereInItsSubtree {
        private final List<Context> subcontexts = subContexts(ContextClasses.NestedExamples.class);
        
        @Test
        public void returnsASubContextForTheClass() {
          assertContextClasses(subcontexts, ImmutableList.of(
            ContextClasses.NestedExamples.middleWithNoTests.class, ContextClasses.NestedExamples.middleWithTest.class));
        }
        
        @Test
        public void includesExampleNamesForItFieldsDeclaredInThatClass() {
          assertExampleNames(subcontexts.get(0));
          assertExampleNames(subcontexts.get(1), "middle_test");
        }
      }
    }
  }
  
  public class hasExamples {
    public class givenAClassWithoutAnyExamples {
      @Test
      public void returnsFalse() {
        ExampleGateway subject = new ClassExampleGateway(ContextClasses.Empty.class);
        assertThat(subject.hasExamples(), equalTo(false));
      }
    }
    
    public class givenAClassWith1OrMoreExamples {
      @Test
      public void returnsTrue() {
        ExampleGateway subject = new ClassExampleGateway(ContextClasses.OneIt.class);
        assertThat(subject.hasExamples(), equalTo(true));
      }
    }
  }
  
  private static void assertContextClasses(List<Context> contexts, List<Class<?>> classes) {
    assertListEquals(classes, contextClasses(contexts));
  }
  
  private static void assertExampleNames(Context context, String... names) {
    assertListEquals(Arrays.asList(names), context.getExampleNames());
  }
  
  private static List<Class<?>> contextClasses(List<Context> contexts) {
    return contexts.stream().map(x -> (Class<?>)x.id).collect(toList());
  }
  
  private static List<Context> subContexts(Class<?> contextClass) {
    ClassExampleGateway subject = new ClassExampleGateway(contextClass);
    return subject.getSubContexts(subject.getRootContext());
  }
}