package info.javaspec.runner.ng;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspecproto.ContextClasses;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(HierarchicalContextRunner.class)
public class ClassExampleGatewayTest {
  public class hasExamples {
    @Test
    public void givenAContextClassWithNoExamples_returnsFalse() throws Exception {
      shouldNotHaveExamples(ContextClasses.Empty.class);
    }

    @Test
    public void givenAContextClassWith1OrMoreExamples_returnsTrue() throws Exception {
      shouldHaveExamples(ContextClasses.OneIt.class);
    }
  }

  public class totalNumExamples {
    @Test
    public void givenAContextClassWith1OrMoreExamples_returnsTheNumberOfExamples() throws Exception {
      shouldHaveTotalNumExamples(ContextClasses.OneIt.class, 1);
    }

    @Test
    public void givenAContextClassWithNoExamples_returns_0() throws Exception {
      shouldHaveTotalNumExamples(ContextClasses.Empty.class, 0);
    }
  }

  public class givenARootContextClass {
    @Test
    public void withNoFieldsOfTypeIt_hasNoExamples() throws Exception {
      shouldNotHaveExamples(ContextClasses.Empty.class);
      shouldHaveTotalNumExamples(ContextClasses.Empty.class, 0);
    }

    @Test
    public void with1OrMoreOfInstanceFieldsOfTypeIt_hasAnExampleForEachOfThoseFields() throws Exception {
      shouldHaveExamples(ContextClasses.OneIt.class);
      shouldHaveTotalNumExamples(ContextClasses.OneIt.class, 1);
    }

    @Test
    public void withStaticFieldsOfTypeIt_doesNotCountThoseFieldsAsExamples() throws Exception {
      shouldNotHaveExamples(ContextClasses.StaticIt.class);
      shouldHaveTotalNumExamples(ContextClasses.StaticIt.class, 0);
    }

    @Test
    public void withNestedClasses_countsExamplesInThoseClasses() throws Exception {
      shouldHaveTotalNumExamples(ContextClasses.NestedIt.class, 1);
      shouldHaveExamples(ContextClasses.NestedContext.class);

      shouldHaveTotalNumExamples(ContextClasses.NestedThreeDeep.class, 1);
      shouldHaveExamples(ContextClasses.NestedThreeDeep.class);
    }

    @Test
    public void withAStaticInnerClass_doesNotCountItFieldsInThoseClassesAsExamples() throws Exception {
      shouldHaveTotalNumExamples(ContextClasses.NestedStaticClassIt.class, 0);
      shouldNotHaveExamples(ContextClasses.NestedStaticClassIt.class);
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

  private void shouldHaveTotalNumExamples(Class<?> contextClass, long numExamples) {
    NewExampleGateway subject = new ClassExampleGateway(contextClass);
    assertThat(subject.totalNumExamples(), equalTo(numExamples));
  }
}
