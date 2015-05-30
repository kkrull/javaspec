package info.javaspec.runner.ng;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspecproto.ContextClasses;
import org.junit.Test;
import org.junit.runner.RunWith;

import static info.javaspec.testutil.Matchers.matchesRegex;
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

  public class givenARootContextClass {
    @Test
    public void withNoFieldsOfTypeIt_hasNoExamples() throws Exception {
      shouldNotHaveSpecs(ContextClasses.Empty.class);
      shouldHaveSpecCount(ContextClasses.Empty.class, 0);
    }

    @Test
    public void with1OrMoreOfInstanceFieldsOfTypeIt_hasAnExampleForEachOfThoseFields() throws Exception {
      shouldHaveSpecs(ContextClasses.OneIt.class);
      shouldHaveSpecCount(ContextClasses.OneIt.class, 1);
    }

    @Test
    public void withStaticFieldsOfTypeIt_doesNotCountThoseFieldsAsExamples() throws Exception {
      shouldNotHaveSpecs(ContextClasses.StaticIt.class);
      shouldHaveSpecCount(ContextClasses.StaticIt.class, 0);
    }

    @Test
    public void withNestedClasses_countsExamplesInThoseClasses() throws Exception {
      shouldHaveSpecCount(ContextClasses.NestedIt.class, 1);
      shouldHaveSpecs(ContextClasses.NestedContext.class);

      shouldHaveSpecCount(ContextClasses.NestedThreeDeep.class, 1);
      shouldHaveSpecs(ContextClasses.NestedThreeDeep.class);
    }

    @Test
    public void withAStaticInnerClass_doesNotCountItFieldsInThoseClassesAsExamples() throws Exception {
      shouldHaveSpecCount(ContextClasses.NestedStaticClassIt.class, 0);
      shouldNotHaveSpecs(ContextClasses.NestedStaticClassIt.class);
    }
  }

  public class rootContextId {
    @Test
    public void givenAClass_returnsTheFullClassName() throws Exception {
      SpecGateway subject = new ClassSpecGateway(ContextClasses.OneIt.class);
      assertThat(subject.rootContextId(), matchesRegex("^.*[.]ContextClasses[$]OneIt$"));
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

  private void shouldHaveSpecCount(Class<?> contextClass, long numSpecs) {
    SpecGateway subject = new ClassSpecGateway(contextClass);
    assertThat(subject.countSpecs(), equalTo(numSpecs));
  }
}
