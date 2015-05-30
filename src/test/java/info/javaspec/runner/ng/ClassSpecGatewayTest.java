package info.javaspec.runner.ng;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspecproto.ContextClasses;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@RunWith(HierarchicalContextRunner.class)
public class ClassSpecGatewayTest {
  public class countSpecs {
    @Test
    public void givenAContextClassWithNoSpecs_returns_0() throws Exception {
      shouldHaveSpecCount(ContextClasses.Empty.class, 0);
    }

    @Test
    public void givenAContextClassWith1OrMoreSpecs_returnsTheNumberOfSpecs() throws Exception {
      shouldHaveSpecCount(ContextClasses.OneIt.class, 1);
    }
  }

  public class hasSpecs {
    @Test
    public void givenAContextClassWithNoSpecs_returnsFalse() throws Exception {
      shouldNotHaveSpecs(ContextClasses.Empty.class);
    }

    @Test
    public void givenAContextClassWith1OrMoreSpecs_returnsTrue() throws Exception {
      shouldHaveSpecs(ContextClasses.OneIt.class);
    }
  }

  private void shouldHaveSpecs(Class<?> rootContextClass) {
    SpecGateway subject = new ClassSpecGateway(rootContextClass);
    assertThat(subject.hasSpecs(), is(true));
  }

  private void shouldNotHaveSpecs(Class<?> rootContextClass) {
    SpecGateway subject = new ClassSpecGateway(rootContextClass);
    assertThat(subject.hasSpecs(), is(false));
  }

  private void shouldHaveSpecCount(Class<?> contextClass, long numExamples) {
    SpecGateway subject = new ClassSpecGateway(contextClass);
    assertThat(subject.countSpecs(), equalTo(numExamples));
  }
}
