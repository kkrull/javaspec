package info.javaspec.runner.ng;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.dsl.It;
import info.javaspecproto.ContextClasses;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static com.google.common.collect.Lists.newArrayList;
import static info.javaspec.testutil.Matchers.matchesRegex;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

@RunWith(HierarchicalContextRunner.class)
public class ClassSpecGatewayTest {
  private SpecGateway<ClassContext> subject;
  private ClassSpecGateway.LambdaSpecFactory specFactory = mock(ClassSpecGateway.LambdaSpecFactory.class);

  @Before
  public void setup() throws Exception {
    givenSpecFactoryMakes(mock(Spec.class, "DefaultSpec"));
  }

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
      subject = new ClassSpecGateway(ContextClasses.OneIt.class, specFactory);
      assertThat(subject.rootContextId(), matchesRegex("^.*[.]ContextClasses[$]OneIt$"));
    }
  }

  public class rootContext {
    private Context returned;

    @Before
    public void setup() throws Exception {
      subject = new ClassSpecGateway(ContextClasses.NestedBehavior.describes_some_conditions.class, specFactory);
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
    @Test
    public void givenAClassWithNoInnerClasses_returnsEmpty() {
      subject = new ClassSpecGateway(ContextClasses.OneIt.class, specFactory);
      List<ClassContext> returned = subject.getSubcontexts(subject.rootContext());
      assertThat(returned, hasSize(0));
    }

    @Test
    public void givenAClassWith1OrMoreInnerClasses_returnsAContextForEachClass() {
      subject = new ClassSpecGateway(ContextClasses.NestedThreeDeep.class, specFactory);
      shouldHaveSubcontexts(subject.rootContext(), "info.javaspecproto.ContextClasses$NestedThreeDeep$middle");
      shouldHaveSubcontexts(onlySubcontext(subject.rootContext()), "info.javaspecproto.ContextClasses$NestedThreeDeep$middle$bottom");
    }

    @Test
    public void givenAClassWithStaticHelperClasses_ignoresItFieldsInThatClass() {
      subject = new ClassSpecGateway(ContextClasses.NestedStaticClassIt.class, specFactory);
      shouldHaveSubcontexts(subject.rootContext());
    }

    public class givenAContextClass {
      private Context returned;

      @Before
      public void setup() {
        subject = new ClassSpecGateway(ContextClasses.NestedBehavior.class, specFactory);
        List<ClassContext> subcontexts = subject.getSubcontexts(subject.rootContext());
        returned = subcontexts.get(0);
      }

      @Test
      public void givenAClassNameInSnakeCase_replacesUnderscoresWithSpaces() {
        assertThat(returned.displayName, equalTo("describes some conditions"));
      }
    }

    private void shouldHaveSubcontexts(ClassContext context, String... ids) {
      List<String> actualIds = subject.getSubcontexts(context).stream().map(x -> x.id).collect(toList());
      assertThat(actualIds, equalTo(Arrays.asList(ids)));
    }
  }

  public class getSpecs {
    @Test
    public void givenASpec_returnsThatSpec() {
      Spec factoryMade = mock(Spec.class, "FactoryMade");
      givenSpecFactoryMakes("info.javaspecproto.ContextClasses.OneIt.only_test", factoryMade);
      subject = new ClassSpecGateway(ContextClasses.OneIt.class, specFactory);
      assertThat(subject.getSpecs(subject.rootContext()), equalTo(newArrayList(factoryMade)));
    }

    @Test
    public void givenAClassWithNoInstanceItFields_returnsEmpty() throws Exception {
      subject = new ClassSpecGateway(ContextClasses.Empty.class, specFactory);
      assertThat(subject.getSpecs(subject.rootContext()), hasSize(0));
    }

    @Test
    public void givenAClassWithInstanceItFields_returnsASpecForEachField() {
      subject = new ClassSpecGateway(ContextClasses.TwoIt.class, specFactory);
      subject.getSpecs(subject.rootContext());
      shouldHaveMadeSpecs("info.javaspecproto.ContextClasses.TwoIt.first_test",
        "info.javaspecproto.ContextClasses.TwoIt.second_test");

      subject = new ClassSpecGateway(ContextClasses.NestedContext.class, specFactory);
      subject.getSpecs(onlySubcontext(subject.rootContext()));
      shouldHaveMadeSpecs("info.javaspecproto.ContextClasses.NestedContext.inner.asserts");
    }

    @Test
    public void givenAClassWithStaticItFields_ignoresThoseFields() {
      subject = new ClassSpecGateway(ContextClasses.StaticIt.class, specFactory);
      shouldHaveSpecs(subject.rootContext());
    }

    //TODO KDK: Refactor these tests to work with verifying calls to specFactory
    public class givenAnInstanceItField {
      private Spec toReturn = mock(Spec.class);
      private Spec returned;

      @Before
      public void setup() throws Exception {
        when(specFactory.makeSpec(Mockito.anyString(), Mockito.anyString(), Mockito.any()))
          .thenReturn(toReturn);
        subject = new ClassSpecGateway(ContextClasses.OneIt.class, specFactory);
        returned = subject.getSpecs(subject.rootContext()).get(0);
      }

      @Test
      public void identifiesTheSpecByTheFullyQualifiedPathToThatField() {
        verify(specFactory).makeSpec(Mockito.eq("info.javaspecproto.ContextClasses.OneIt.only_test"),
          Mockito.any(), Mockito.any());
      }

      @Test //TODO KDK: Move to givenASpec?
      public void humanizesSnakeCasedFieldNamesByReplacingUnderscoresWithSpaces() {
        verify(specFactory).makeSpec(Mockito.anyString(), Mockito.eq("only test"), Mockito.any());
      }

      @Test @Ignore
      public void returnsASpecForThatField() {
        verify(specFactory).makeSpec(Mockito.anyString(), Mockito.any(), Mockito.any(It.class));
        assertThat(returned, sameInstance(toReturn));
      }
    }

    private void shouldHaveSpecs(ClassContext context, String... ids) {
      List<String> actualIds = subject.getSpecs(context).stream().map(x -> x.id).collect(toList());
      assertThat(actualIds, equalTo(Arrays.asList(ids)));
    }
  }

  private void givenSpecFactoryMakes(String id, Spec spec) {
    when(specFactory.makeSpec(Mockito.eq(id), Mockito.anyString(), Mockito.any(It.class)))
      .thenReturn(spec);
  }

  private void givenSpecFactoryMakes(Spec spec) {
    when(specFactory.makeSpec(Mockito.anyString(), Mockito.anyString(), Mockito.any(It.class)))
      .thenReturn(spec);
  }

  private ClassContext onlySubcontext(ClassContext parent) {
    List<ClassContext> children = subject.getSubcontexts(parent);
    if(children.size() != 1) {
      String msg = String.format("Expected context %s to have 1 child, but had %d", parent.id, children.size());
      throw new RuntimeException(msg);
    }

    return children.get(0);
  }

  private void shouldHaveSpecs(Class<?> rootContextClass) {
    SpecGateway subject = new ClassSpecGateway(rootContextClass, specFactory);
    assertThat(subject.hasSpecs(), is(true));
  }

  private void shouldNotHaveSpecs(Class<?> rootContextClass) {
    SpecGateway subject = new ClassSpecGateway(rootContextClass, specFactory);
    assertThat(subject.hasSpecs(), is(false));
  }

  private void shouldHaveSpecCount(Class<?> contextClass, long numSpecs) {
    SpecGateway subject = new ClassSpecGateway(contextClass, specFactory);
    assertThat(subject.countSpecs(), equalTo(numSpecs));
  }

  private void shouldHaveMadeSpecs(String... ids) {
    Stream.of(ids).forEach(id -> verify(specFactory, times(1))
      .makeSpec(
        Mockito.eq(id),
        Mockito.anyString(), Mockito.any()
      ));
  }
}
