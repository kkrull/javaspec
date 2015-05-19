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
    public void givenAClassWithoutAnyItFields_returnsFalse() throws Exception {
      shouldNotHaveExamples(ContextClasses.Empty.class);
    }

    @Test
    public void givenAClassWithOneOrMoreOfItsOwnItFields_returnsTrue() throws Exception {
      shouldHaveExamples(ContextClasses.OneIt.class);
    }

    @Test
    public void givenAClassWithOneOrMoreItFieldsInADescendantNestedClass_returnsTrue() throws Exception {
      shouldHaveExamples(ContextClasses.NestedContext.class);
      shouldHaveExamples(ContextClasses.NestedThreeDeep.class);
    }

    @Test @Ignore
    public void itFieldsInStaticInnerClassesDoNotCount() throws Exception {
      assertThat("pending", equalTo("passing"));
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
