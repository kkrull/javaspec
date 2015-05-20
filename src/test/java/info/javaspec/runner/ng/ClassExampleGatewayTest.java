package info.javaspec.runner.ng;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.testutil.Assertions;
import info.javaspecproto.ContextClasses;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(HierarchicalContextRunner.class)
public class ClassExampleGatewayTest {
  //TODO KDK: Really shouldn't allow a static class to run
  public class constructor {
    @Test @Ignore
    public void givenAStaticInnerClass_throwsStatic() throws Exception {
      Assertions.capture(ClassExampleGateway.StaticContextException.class,
        () -> new ClassExampleGateway(ContextClasses.StaticClassIt.Helper.class));
    }
  }

  public class hasExamples {
    @Test
    public void givenAClassWithoutAnyItFields_returnsFalse() throws Exception {
      shouldNotHaveExamples(ContextClasses.Empty.class);
    }

    @Test
    public void givenAClassWithOneOrMoreOfItsOwnItFields_returnsTrue() throws Exception {
      shouldHaveExamples(ContextClasses.OneIt.class);
    }

    @Test
    public void givenAClassWithOneOrMoreItFieldsInAnInnerClass_returnsTrue() throws Exception {
      shouldHaveExamples(ContextClasses.NestedContext.class);
      shouldHaveExamples(ContextClasses.NestedThreeDeep.class);
    }

    //Cross-cutting stuff like this can be refactored - staticItIsNotSupported.
    //hasExamples and numExamples: givenASupportedTest_returnsTrue, etc...
    @Test
    public void givenAStaticInnerClassWithItFields_returnsFalse() throws Exception {
      //Tests in static classes aren't supported because there's no instances of outer classes with which to associate.
      //Just because you could tag a static inner class @RunWith and the runner might not know the difference, it's inconsistent and doesn't allow for surrounding context to be added later.
      //So it's not supported at this time, for consistency with usage and consistency in the progression of use.
      shouldNotHaveExamples(ContextClasses.StaticClassIt.class);
    }

    @Test @Ignore
    public void givenAClassWithAStaticItField_returnsFalse() throws Exception {
      //Static It fields aren't supported because the notion of starting the test with a clean slate breaks down
      //Shouldn't this lead to an InitializationError in JUnit?
    }
  }

  private void shouldHaveExamples(Class<?> rootContextClass) {
    NewExampleGateway subject = new ClassExampleGateway(rootContextClass);
    assertThat(subject.hasExamples(), is(true));
  }

  private void shouldNotHaveExamples(Class<?> contextClass) {
    NewExampleGateway subject = new ClassExampleGateway(contextClass);
    assertThat(subject.hasExamples(), is(false));
  }
}
