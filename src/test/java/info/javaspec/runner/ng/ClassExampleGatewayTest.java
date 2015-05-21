package info.javaspec.runner.ng;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspecproto.ContextClasses;
import org.junit.Ignore;
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
      shouldTotalNumExamples(ContextClasses.OneIt.class, 1);
    }

    @Test
    public void givenAContextClassWithNoExamples_returns_0() throws Exception {
      shouldTotalNumExamples(ContextClasses.Empty.class, 0);
    }
  }

  public class givenAContextClassWith {
    @Test
    public void noFieldsOfTypeIt_hasNoExamples() throws Exception {
      shouldNotHaveExamples(ContextClasses.Empty.class);
    }

    @Test
    public void oneOrMoreOfInstanceFieldsOfTypeIt_hasAnExampleForEachOfThoseFields() throws Exception {
      shouldHaveExamples(ContextClasses.OneIt.class);
    }

    @Test @Ignore
    public void staticFieldsOfTypeIt_doesNotCountThoseFieldsAsExamples() throws Exception {
    }

    @Test
    public void aNestedClassThatHasOneOrMoreInstanceFieldsOfTypeIt_hasAnExampleForEachOfThoseFields() throws Exception {
      shouldHaveExamples(ContextClasses.NestedContext.class);
      shouldHaveExamples(ContextClasses.NestedThreeDeep.class);
    }

    @Test
    public void aStaticInnerClassWithFieldsOfTypeIt_doesNotCountThoseFieldsAsExamples() throws Exception {
      shouldNotHaveExamples(ContextClasses.StaticClassIt.class);
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

  private void shouldTotalNumExamples(Class<?> contextClass, int numExamples) {
    NewExampleGateway subject = new ClassExampleGateway(contextClass);
    assertThat(subject.totalNumExamples(), equalTo(numExamples));
  }
}
