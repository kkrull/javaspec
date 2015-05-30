package info.javaspec.runner.ng;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspecproto.ContextClasses;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

@Ignore
@RunWith(HierarchicalContextRunner.class)
public class ClassExampleGatewayTest {
//  private NewExampleGateway subject;

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
    private List<Description> children;

    @Test //The top-level class doesn't describe behavior; it's just a container that has to end in the word Test
    public void setsTheTopLevelDescriptionToTheSimpleNameOfTheRootClass() throws Exception {
      description = junitDescriptionFor(ContextClasses.OneIt.class);
      assertThat(description.getClassName(), equalTo("OneIt"));
    }

    public class givenARootContextClassWithNoItFields_andNoDescendantInnerClasses {
      @Test
      public void returnsThe_EMPTY_Description() throws Exception {
        assertThat(junitDescriptionFor(ContextClasses.Empty.class), sameInstance(Description.EMPTY));
      }
    }

    public class givenAContextClassWith1OrMoreItFields {
      @Before
      public void setup() throws Exception {
        description = junitDescriptionFor(ContextClasses.TwoIt.class);
        children = description.getChildren();
      }

      @Test
      public void returnsASuiteDescriptionForThatContextClass() throws Exception {
        assertThat(newArrayList(description.isTest(), description.isSuite()), contains(false, true));
        assertThat(description.getClassName(), equalTo("TwoIt"));
      }

      @Test
      public void theSuiteDescriptionContainsATestDescriptionForEachExample() throws Exception {
        assertThat(children.stream().map(Description::isTest).collect(toList()), contains(true, true));
        assertThat(children.stream().map(Description::getClassName).collect(toList()), contains("TwoIt", "TwoIt"));
      }

      @Test
      public void eachTestDescriptionNameIsTheHumanizedVersionOfTheItFieldName() throws Exception {
        assertThat(children.stream().map(Description::getMethodName).collect(toList()), contains("first test", "second test"));
      }
    }

    public class givenAContextClassWithInnerClasses {
      @Before
      public void setup() throws Exception {
        description = junitDescriptionFor(ContextClasses.NestedBehavior.class);
        children = description.getChildren();
      }

      @Test
      public void returnsSuiteDescriptionsForThoseClasses() throws Exception {
        assertThat(children, hasSize(1));
        Description descriptiveContext = children.get(0);
        assertThat(newArrayList(descriptiveContext.isTest(), descriptiveContext.isSuite()), contains(false, true));
      }

      @Test
      public void describesContextBehaviorWithTheHumanizedContextClassName() throws Exception {
        Description descriptiveContext = children.get(0);
        assertThat(descriptiveContext.getClassName(), equalTo("describes some conditions"));
      }
    }
  }

  public class rootContextName {
    @Test
    public void returnsTheClassSimpleName_sinceItDescribesATestClassAndNotAContext() throws Exception {
//      subject = new ClassExampleGateway(ContextClasses.OneIt.class);
//      assertThat(subject.rootContextName(), equalTo("OneIt"));
    }
  }

  private Description junitDescriptionFor(Class<?> rootContext) {
//    subject = new ClassExampleGateway(rootContext);
//    return subject.junitDescriptionTree();
    throw new UnsupportedOperationException();
  }

  private void shouldHaveExamples(Class<?> rootContextClass) {
//    NewExampleGateway subject = new ClassExampleGateway(rootContextClass);
//    assertThat(subject.hasExamples(), is(true));
  }

  private void shouldNotHaveExamples(Class<?> contextClass) {
//    NewExampleGateway subject = new ClassExampleGateway(contextClass);
//    assertThat(subject.hasExamples(), is(false));
  }

  private void shouldHaveTotalNumExamples(Class<?> contextClass, long numExamples) {
//    NewExampleGateway subject = new ClassExampleGateway(contextClass);
//    assertThat(subject.totalNumExamples(), equalTo(numExamples));
  }
}
