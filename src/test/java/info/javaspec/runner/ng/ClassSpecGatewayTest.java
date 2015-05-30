package info.javaspec.runner.ng;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspecproto.ContextClasses;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static info.javaspec.testutil.Matchers.matchesRegex;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@RunWith(HierarchicalContextRunner.class)
public class ClassSpecGatewayTest {
  private SpecGateway subject;

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
    public void withNoFieldsOfTypeIt_hasNoSpecs() throws Exception {
      shouldNotHaveSpecs(ContextClasses.Empty.class);
      shouldHaveSpecCount(ContextClasses.Empty.class, 0);
    }

    @Test
    public void with1OrMoreOfInstanceFieldsOfTypeIt_hasASpecForEachOfThoseFields() throws Exception {
      shouldHaveSpecs(ContextClasses.OneIt.class);
      shouldHaveSpecCount(ContextClasses.OneIt.class, 1);
    }

    @Test
    public void withStaticFieldsOfTypeIt_doesNotCountThoseFieldsAsSpecs() throws Exception {
      shouldNotHaveSpecs(ContextClasses.StaticIt.class);
      shouldHaveSpecCount(ContextClasses.StaticIt.class, 0);
    }

    @Test
    public void withNestedClasses_countsSpecsInThoseClasses() throws Exception {
      shouldHaveSpecCount(ContextClasses.NestedIt.class, 1);
      shouldHaveSpecs(ContextClasses.NestedContext.class);

      shouldHaveSpecCount(ContextClasses.NestedThreeDeep.class, 1);
      shouldHaveSpecs(ContextClasses.NestedThreeDeep.class);
    }

    @Test
    public void withAStaticInnerClass_doesNotCountItFieldsInThoseClassesAsSpecs() throws Exception {
      shouldHaveSpecCount(ContextClasses.NestedStaticClassIt.class, 0);
      shouldNotHaveSpecs(ContextClasses.NestedStaticClassIt.class);
    }
  }

  public class rootContextId {
    @Test
    public void givenAClass_returnsTheFullClassName() throws Exception {
      subject = new ClassSpecGateway(ContextClasses.OneIt.class);
      assertThat(subject.rootContextId(), matchesRegex("^.*[.]ContextClasses[$]OneIt$"));
    }
  }

  public class rootContext {
    private Context returned;

    @Before
    public void setup() throws Exception {
      subject = new ClassSpecGateway(ContextClasses.NestedBehavior.describes_some_conditions.class);
      returned = subject.rootContext();
    }

    @Test
    public void returnsAContextForTheGivenClass() throws Exception {
      assertThat(returned.id, matchesRegex("^.*[.]ContextClasses[$]NestedBehavior[$]describes_some_conditions$"));
    }

    //The root context class is just a container.  It doesn't describe behavior, so its name doesn't get humanized.
    @Test
    public void usesTheSimpleNameForTheDisplayName() {
      assertThat(returned.displayName, equalTo("describes_some_conditions"));
    }
  }

  public class getSubcontexts {
    private List<Context> returned;

    @Test
    public void givenAClassWithNoInnerClasses_returnsEmpty() {
      subject = new ClassSpecGateway(ContextClasses.OneIt.class);
      returned = subject.getSubcontexts(subject.rootContext());
      assertThat(returned, hasSize(0));
    }

    @Test
    public void givenAClassWith1OrMoreInnerClasses_returnsAContextForEachClass() {
      subject = new ClassSpecGateway(ContextClasses.NestedContext.class);
      returned = subject.getSubcontexts(subject.rootContext());
      assertThat(returned, hasSize(1));
    }

    @Test
    public void givenAClassNameInSnakeCase_replacesUnderscoresWithSpaces() {
      subject = new ClassSpecGateway(ContextClasses.NestedBehavior.class);
      returned = subject.getSubcontexts(subject.rootContext());
      Context subContext = returned.get(0);
      assertThat(subContext.displayName, equalTo("describes some conditions"));
    }
  }

  public class getSpecs {
    @Test
    public void givenAClassWithNoInstanceItFields_returnsEmpty() throws Exception {
      subject = new ClassSpecGateway(ContextClasses.Empty.class);
      assertThat(subject.getSpecs(subject.rootContext()), hasSize(0));
    }

    @Test
    public void givenAClassWithInstanceItFields_returnsASpecForEachField() {
      subject = new ClassSpecGateway(ContextClasses.TwoIt.class);
      assertThat(subject.getSpecs(subject.rootContext()), hasSize(2));
    }

    public class givenASpec {
      private Spec returned;

      @Before
      public void setup() throws Exception {
        subject = new ClassSpecGateway(ContextClasses.OneIt.class);
        returned = subject.getSpecs(subject.rootContext()).get(0);
      }

      @Test
      public void identifiesTheSpecByTheDeclaredFieldName() {
        assertThat(returned.id, equalTo("only_test"));
      }

      @Test
      public void humanizesSnakeCasedFieldNamesByReplacingUnderscoresWithSpaces() {
        assertThat(returned.displayName, equalTo("only test"));
      }
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
