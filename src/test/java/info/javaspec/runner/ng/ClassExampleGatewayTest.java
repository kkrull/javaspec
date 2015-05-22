package info.javaspec.runner.ng;

import com.google.common.collect.Lists;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspecproto.ContextClasses;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(HierarchicalContextRunner.class)
public class ClassExampleGatewayTest {
  private NewExampleGateway subject;

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

  public class junitDescriptionTree {
    private Description description;

    public class givenAClassWith1ItFieldAndNoInnerClasses {
      @Before
      public void setup() throws Exception {
        subject = new ClassExampleGateway(ContextClasses.OneIt.class);
        description = subject.junitDescriptionTree();
      }

      @Test
      public void returnsATestDescription() throws Exception {
        assertThat(Lists.newArrayList(description.isTest(), description.isSuite()), contains(true, false));
      }

      @Test
      public void hasOneTest() throws Exception {
        assertThat(description.isEmpty(), is(false));
        assertThat(description.testCount(), equalTo(1));
      }

      @Test
      public void setsTheClassNameAnTheContextClassName() throws Exception {
        assertThat(description.getClassName(), equalTo("OneIt"));
      }

      @Test
      public void setsTheMethodNameToTheFieldName_replacingUnderscoreWithSpace() throws Exception {
        assertThat(description.getMethodName(), equalTo("only test"));
      }
    }

    public class givenAClassWithNestedClasses {
      @Test @Ignore
      public void returnsDescriptionsForThoseClasses() throws Exception {
      }
    }
  }

  public class rootContextName {
    @Test
    public void returnsTheClassSimpleName_sinceItDescribesATestClassAndNotAContext() throws Exception {
      subject = new ClassExampleGateway(ContextClasses.OneIt.class);
      assertThat(subject.rootContextName(), equalTo("OneIt"));
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
