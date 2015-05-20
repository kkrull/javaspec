package info.javaspec.runner;

import com.google.common.collect.ImmutableSet;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.runner.ClassExampleGateway.UnknownStepExecutionSequenceException;
import info.javaspecproto.ContextClasses;
import info.javaspecproto.ContextClasses.NestedContextAndStaticHelperClass;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

@RunWith(HierarchicalContextRunner.class)
public class ClassExampleGatewayTest {
  public class findInitializationErrors {
    public class givenAnyTreeOfInnerClasses {
      @Test
      public void andAClassWith2OrMoreEstablishFields_containsUnknownStepExecutionSequenceException() {
        shouldFindInitializationError(ContextClasses.TwoEstablish.class, UnknownStepExecutionSequenceException.class,
          "Impossible to determine running order of multiple Establish functions in test context info.javaspecproto.ContextClasses$TwoEstablish");
      }
      
      @Test
      public void andAClassWith2OrMoreBecauseFields_containsUnknownStepExecutionSequenceException() {
        shouldFindInitializationError(ContextClasses.TwoBecause.class, UnknownStepExecutionSequenceException.class,
          "Impossible to determine running order of multiple Because functions in test context info.javaspecproto.ContextClasses$TwoBecause");
      }
      
      @Test
      public void andAClassWith2OrMoreCleanupFields_containsUnknownStepExecutionSequenceException() {
        shouldFindInitializationError(ContextClasses.TwoCleanup.class, UnknownStepExecutionSequenceException.class,
          "Impossible to determine running order of multiple Cleanup functions in test context info.javaspecproto.ContextClasses$TwoCleanup");
      }
      
      public class andAtLeast1ItFieldSomewhere_andNoClassesWith2OrMoreOfTheSameFixture {
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
    private @Mock ClassExampleGateway.ExampleFactory factory;
    private @Captor ArgumentCaptor<List<Field>> befores;
    private @Captor ArgumentCaptor<List<Field>> afters;
    
    @Before
    public void initMocks() { MockitoAnnotations.initMocks(this); }
    
    /* Context definition: A group of related examples and/or test fixtures.  All examples in the same context share
     * the same test fixture.
     * 
     * Instance of a context: An environment or state in which an example runs, as maintained by the example's fixture.
     * 
     * Here, the context is defined by a (sub-)tree of classes rooted at the given context class and including all its
     * descendant, inner classes.  An instance of a context is the set of instances of these classes.  Lambdas execute
     * in an instance of the class in which it is declared.
     * 
     * Note that the top-level context class may be a static class, but the context does not include any static classes
     * enclosing or enclosed within this class.
     */

    public class defineContext {
      public class givenAClassWith1OrMoreNestedStaticClasses {
        @Test
        public void doesNotCreateExamplesForItFieldsDeclaredInAStaticNestedClass() {
          assertThat(extractNames(readExamples(NestedContextAndStaticHelperClass.class)), contains("asserts"));
        }
      }
    }
    
    /* Example: A single test.  Consists of a thunk assigned to an It field and 0..n thunks for its test fixture. */
    
    public class defineExample {
      public class givenNoItFieldsWithinTheContext {
        @Test
        public void returnsNoExamples() {
          assertThat(readExamples(ContextClasses.Empty.class), empty());
        }
      }
      
      public class given1OrMoreItFieldsWithinTheContext {
        @Test
        public void returnsAnExampleForEachItField() {
          readExamples(ContextClasses.NestedExamples.class, factory);
          assertCreatedExample(ContextClasses.NestedExamples.class, "top_level_test");
          assertCreatedExample(ContextClasses.NestedExamples.middleWithNoTests.bottom.class, "bottom_test");
          assertCreatedExample(ContextClasses.NestedExamples.middleWithTest.class, "middle_test");
          assertCreatedExample(ContextClasses.NestedExamples.middleWithTest.bottom.class, "another_bottom_test");
          Mockito.verifyNoMoreInteractions(factory);
        }
      }
    }
    
    /* Fixture: Optional thunks running before an example to set up conditions necessary for the test and/or after
     * an example to restore the environment to its original state.
     */
    
    public class defineFixture {
      public class givenNoFixtureFieldsInTheContext {
        @Test
        public void createdExamplesHaveNoFixture() {
          readExamples(ContextClasses.OneIt.class, factory);
          assertEmptyFixture(ContextClasses.OneIt.class, "only_test");
        }
      }
      
      public class givenFixtureFieldsOutsideTheContext {
        @Test
        public void excludesTheseFieldsFromTheContext() {
          readExamples(ContextClasses.NestedFixture.targetContext.class, factory);
          assertEmptyFixture(ContextClasses.NestedFixture.targetContext.class, "asserts_in_target_context");
          assertBefores(ContextClasses.NestedFixture.targetContext.moreSpecificContext.class, 
            "asserts_in_more_specific_context",
            field(ContextClasses.NestedFixture.targetContext.moreSpecificContext.class, "below_target_context"));
        }
      }
      
      public class given2OrMoreItFieldsInTheSameScopeAnd1OrMoreFixtureFieldsVisibibleInThatScope {
        private final ArgumentMatcher<Field> establish = field(ContextClasses.TwoItWithEstablish.class, "that");
        
        @Test
        public void allExamplesInTheSameScopeGetTheSameFixture() {
          readExamples(ContextClasses.TwoItWithEstablish.class, factory);
          assertBefores(ContextClasses.TwoItWithEstablish.class, "does_one_thing", establish);
          assertBefores(ContextClasses.TwoItWithEstablish.class, "does_something_else", establish);
        }
      }
      
      public class givenUpTo1EstablishLambdaInEachLevelOfContext {
        @Test
        public void theseBecomeBeforeLambdasThatRunOuterContextToInnerContext() {
          readExamples(ContextClasses.NestedEstablish.class, factory);
          assertBefores(ContextClasses.NestedEstablish.inner.class, "asserts",
            field(ContextClasses.NestedEstablish.class, "outer_arrange"),
            field(ContextClasses.NestedEstablish.inner.class, "inner_arrange"));
        }
      }
      
      public class givenUpTo1BecauseLambdaInEachLevelOfContext {
        @Test
        public void theseBecomeBeforeLambdasThatRunOuterContextToInnerContext() {
          readExamples(ContextClasses.NestedBecause.class, factory);
          assertBefores(ContextClasses.NestedBecause.inner.class, "asserts",
            field(ContextClasses.NestedBecause.class, "outer_act"),
            field(ContextClasses.NestedBecause.inner.class, "inner_act"));
        }
      }
      
      public class givenAContextWithEstablishAndBecauseLambdas {
        @Test
        public void beforeLambdasOrderEstablishBecause_thenByContextLevel() {
          readExamples(ContextClasses.NestedEstablishBecause.class, factory);
          assertBefores(ContextClasses.NestedEstablishBecause.inner.class, "asserts",
            field(ContextClasses.NestedEstablishBecause.class, "outer_arrange"),
            field(ContextClasses.NestedEstablishBecause.class, "outer_act"),
            field(ContextClasses.NestedEstablishBecause.inner.class, "inner_arrange"),
            field(ContextClasses.NestedEstablishBecause.inner.class, "inner_act"));
        }
      }
      
      public class givenUpTo1CleanupLambdaInEachLevelOfContext {
        @Test
        public void theseBecomeAfterLambdasThatRunInnerContextToOuterContext() {
          readExamples(ContextClasses.NestedCleanup.class, factory);
          assertAfters(ContextClasses.NestedCleanup.inner.class, "asserts",
            field(ContextClasses.NestedCleanup.inner.class, "inner_cleanup"),
            field(ContextClasses.NestedCleanup.class, "outer_cleanup"));
        }
      }
    }
    
    private void assertCreatedExample(Class<?> contextClass, String itName) {
      verify(factory).makeExample(Mockito.eq(contextClass), Mockito.argThat(field(contextClass, itName)), 
        Mockito.any(), Mockito.any());
    }
    
    @SafeVarargs
    private final void assertAfters(Class<?> itContext, String itName, Matcher<Field>... afterMatchers) {
      verify(factory).makeExample(
        Mockito.eq(itContext), Mockito.argThat(field(itContext, itName)), 
        befores.capture(), afters.capture());
      assertThat(afters.getValue(), contains(afterMatchers));
    }
    
    @SafeVarargs
    private final void assertBefores(Class<?> itContext, String itName, Matcher<Field>... beforeMatchers) {
      verify(factory).makeExample(
        Mockito.eq(itContext), Mockito.argThat(field(itContext, itName)), 
        befores.capture(), afters.capture());
      assertThat(befores.getValue(), contains(beforeMatchers));
    }
    
    private void assertEmptyFixture(Class<?> itContext, String itName) {
      verify(factory).makeExample(
        Mockito.eq(itContext), Mockito.argThat(field(itContext, itName)), 
        befores.capture(), afters.capture());
      assertThat(befores.getValue(), empty());
      assertThat(afters.getValue(), empty());
    }
    
    private Set<String> extractNames(Collection<Example> examples) {
      return examples.stream().map(Example::getName).collect(toSet());
    }
    
    private Set<Example> readExamples(Class<?> context, ClassExampleGateway.ExampleFactory factory) {
      ExampleGateway subject = new ClassExampleGateway(context, factory);
      return subject.getExamples().collect(toSet());
    }
    
    private Set<Example> readExamples(Class<?> context) {
      ExampleGateway subject = new ClassExampleGateway(context);
      return subject.getExamples().collect(toSet());
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
        assertContextClasses(subContexts(ContextClasses.Empty.class), ImmutableSet.of());
      }
    }
    
    public class givenAClassWith1OrMoreNestedClasses {
      public class whenANestedClassIsStatic {
        @Test
        public void doesNotMakeACorrespondingSubcontext() {
          assertContextClasses(subContexts(NestedContextAndStaticHelperClass.class),
            ImmutableSet.of(NestedContextAndStaticHelperClass.context.class));
        }
      }
      
      public class whenANestedInnerClassHasNoItFieldsInItsSubtree {
        @Test
        public void doesNotMakeACorrespondingSubcontext() {
          assertContextClasses(subContexts(ContextClasses.NestedContextAndInnerHelperClass.class),
            ImmutableSet.of(ContextClasses.NestedContextAndInnerHelperClass.context.class));
        }
      }
      
      public class whenANestedClassIsAnInnerClassWithAnItFieldSomewhereInItsSubtree {
        private final Set<Context> subcontexts = subContexts(ContextClasses.NestedExamples.class);
        
        @Test
        public void returnsASubContextForTheClass() {
          assertContextClasses(subcontexts, ImmutableSet.of(
            ContextClasses.NestedExamples.middleWithNoTests.class, ContextClasses.NestedExamples.middleWithTest.class));
        }
        
        @Test
        public void includesExampleNamesForItFieldsDeclaredInThatClass() {
          assertExampleNames(contextNamed("middleWithNoTests"));
          assertExampleNames(contextNamed("middleWithTest"), "middle_test");
        }
        
        private Context contextNamed(String name) {
          return subcontexts.stream().filter(x -> name.equals(x.name)).findFirst().get();
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
  
  private static void assertContextClasses(Set<Context> contexts, Set<Class<?>> classes) {
    assertThat(contextClasses(contexts), equalTo(classes));
  }
  
  private static void assertExampleNames(Context context, String... names) {
    assertThat(newHashSet(context.getExampleNames()), equalTo(newHashSet(names)));
  }
  
  private static Set<Class<?>> contextClasses(Set<Context> contexts) {
    return contexts.stream().map(x -> (Class<?>)x.id).collect(toSet());
  }
  
  private static Set<Context> subContexts(Class<?> contextClass) {
    ClassExampleGateway subject = new ClassExampleGateway(contextClass);
    return newHashSet(subject.getSubContexts(subject.getRootContext()));
  }
  
  private static ArgumentMatcher<Field> field(Class<?> declaringClass, String name) { 
    return new FieldMatcher(declaringClass, name);
  }
  
  private static class FieldMatcher extends ArgumentMatcher<Field> {
    private final Class<?> declaringClass;
    private final String name;

    public FieldMatcher(Class<?> declaringClass, String name) {
      this.declaringClass = declaringClass;
      this.name = name;
    }
    
    @Override
    public void describeTo(Description description) {
      description.appendText(String.format("<Field %s.%s>", declaringClass.getName(), name));
    }

    @Override
    public boolean matches(Object obj) {
      if (obj == null || obj instanceof Field == false)
        return false;

      Field given = (Field) obj;
      return declaringClass.equals(given.getDeclaringClass())
        && name.equals(given.getName());
    }
  }
}