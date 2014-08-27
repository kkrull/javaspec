package org.javaspec.runner;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.*;
import static org.javaspec.testutil.Assertions.assertListEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.javaspec.proto.ContextClasses;
import org.javaspec.proto.ContextClasses.Empty;
import org.javaspec.runner.ClassExampleGateway.UnknownStepExecutionSequenceException;
import org.javaspec.testutil.Assertions;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

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
    public class givenAClassWithNoItFieldsAtAnyLevel {
      @Test @Ignore("wip")
      public void returnsNoExamples() {
        assertThat(extractNames(readExamples(ContextClasses.Empty.class)), empty());
      }
    }
    
    private List<String> extractNames(Stream<NewExample> examples) {
      return examples.map(NewExample::describeBehavior).collect(toList());
    }
    
    private Stream<NewExample> readExamples(Class<?> context) {
      ExampleGateway subject = new ClassExampleGateway(context);
      return subject.getExamples();
    }
  }
  
  public class getRootContext {
    @Test
    public void returnsAContextNamedAfterTheClass() {
      Context rootContext = doGetRootContext(ContextClasses.OneIt.class);
      assertThat(rootContext.name, equalTo("OneIt"));
    }
    
    @Test
    public void givenAClassDeclaringNoItFields_returnsAContextWithoutExamples() {
      assertHasExamples(ContextClasses.Empty.class);
    }
    
    @Test
    public void givenAClassDeclaring1OrMoreItFields_returnsAContextWithThoseExampleNames() {
      assertHasExamples(ContextClasses.TwoIt.class, "first_test", "second_test");
    }
    
    private void assertHasExamples(Class<?> contextClass, String... names) {
      Context rootContext = doGetRootContext(contextClass);
      assertListEquals(Arrays.asList(names), rootContext.getExampleNames());
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
        Class<?> contextClass = ContextClasses.Empty.class;
        ClassExampleGateway subject = new ClassExampleGateway(contextClass);
        Context context = subject.getRootContext();
        assertThat(subject.getSubContexts(context), empty());
      }
    }
    
    public class givenAnEnclosedStaticClass {
      @Test @Ignore("wip")
      public void doesNotMakeACorrespondingSubcontextForThatClass() {
        fail("pending");
      }
    }
    
    public class givenAnEnclosedInnerClassSubtreeDeclaringNoItFields {
      @Test @Ignore("wip")
      public void doesNotMakeACorrespondingSubcontextForThatClass() {
        fail("pending");
      }
    }
    
    public class given1OrMoreInnerClassSubtreesThatContainAnItField {
      @Test @Ignore("wip")
      public void returnsASubContextForEachSuchClass() {
        fail("pending");
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
}