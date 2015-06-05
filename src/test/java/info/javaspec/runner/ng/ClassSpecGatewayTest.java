package info.javaspec.runner.ng;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspecproto.ContextClasses;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static info.javaspec.testutil.Matchers.matchesRegex;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

@RunWith(HierarchicalContextRunner.class)
public class ClassSpecGatewayTest {
  private SpecGateway<ClassContext> subject;
  private ClassSpecGateway.FieldSpecFactory specFactory = mock(ClassSpecGateway.FieldSpecFactory.class);

  @Before
  public void setup() throws Exception {
    givenSpecFactoryMakes(mock(Spec.class, "DefaultSpec"));
  }

  public class countSpecs {
    @Test
    public void givenAContextClassWithNoSpecs_returns_0() throws Exception {
      shouldNotHaveSpecs(ContextClasses.Empty.class);
    }

    @Test
    public void givenAContextClassWith1OrMoreSpecs_returnsTheNumberOfSpecs() throws Exception {
      shouldHaveSpecs(ContextClasses.OneIt.class, 1);
    }
  }

  public class hasSpecs {
    @Test
    public void givenAContextClassWithNoSpecs_returnsFalse() throws Exception {
      shouldNotHaveSpecs(ContextClasses.Empty.class);
    }

    @Test
    public void givenAContextClassWith1OrMoreSpecs_returnsTrue() throws Exception {
      shouldHaveSpecs(ContextClasses.OneIt.class, 1);
    }
  }

  public class givenARootContextClass {
    @Test
    public void withNoFieldsOfTypeIt_hasNoSpecs() throws Exception {
      shouldNotHaveSpecs(ContextClasses.Empty.class);
      shouldNotReturnAnySpecs(ContextClasses.Empty.class);
    }

    @Test
    public void with1OrMoreOfInstanceFieldsOfTypeIt_hasASpecForEachOfThoseFields() throws Exception {
      shouldHaveSpecs(ContextClasses.OneIt.class, 1);
    }

    @Test
    public void withStaticFieldsOfTypeIt_doesNotCountThoseFieldsAsSpecs() throws Exception {
      shouldNotHaveSpecs(ContextClasses.StaticIt.class);
      shouldNotReturnAnySpecs(ContextClasses.StaticIt.class);
    }

    @Test
    public void withNestedClasses_countsSpecsInThoseClasses() throws Exception {
      shouldHaveSpecs(ContextClasses.NestedIt.class, 1);
      shouldHaveSpecs(ContextClasses.NestedThreeDeep.class, 1);
    }

    @Test
    public void withAStaticInnerClass_doesNotCountItFieldsInThoseClassesAsSpecs() throws Exception {
      shouldNotHaveSpecs(ContextClasses.NestedStaticClassIt.class);
      shouldNotReturnAnySpecs(ContextClasses.NestedStaticClassIt.class);
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
    private @Captor ArgumentCaptor<List<Field>> befores;
    private @Captor ArgumentCaptor<List<Field>> afters;

    @Before
    public void setup() {
      MockitoAnnotations.initMocks(this);
    }

    @Test
    public void givenAClassWithInstanceItFields_returnAFieldSpecForEachField() {
      subject = new ClassSpecGateway(ContextClasses.TwoIt.class);
      List<Class<?>> specClasses = subject.getSpecs(subject.rootContext()).stream()
        .map(Spec::getClass)
        .collect(toList());
      assertThat(specClasses, equalTo(newArrayList(FieldSpec.class, FieldSpec.class)));
    }

    @Test
    public void givenAnInnerClassWithInstanceItFields_returnsASpecForThoseFields() {
      subject = new ClassSpecGateway(ContextClasses.NestedContext.class, specFactory);
      assertThat(subject.getSpecs(onlySubcontext(subject.rootContext())), hasSize(1));
    }

    public class givenAnInstanceItField {
      private Spec toReturn = mock(Spec.class, "Factory Made");
      private List<Spec> returned;

      @Before
      public void setup() throws Exception {
        when(specFactory.makeSpec(Mockito.anyString(), Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.any()))
          .thenReturn(toReturn);
        subject = new ClassSpecGateway(ContextClasses.OneIt.class, specFactory);
        returned = subject.getSpecs(subject.rootContext());
      }

      @Test
      public void identifiesTheSpecByTheFullyQualifiedPathToThatField() {
        verify(specFactory).makeSpec(Mockito.eq("info.javaspecproto.ContextClasses.OneIt.only_test"),
          Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
      }

      @Test
      public void humanizesSnakeCasedFieldNamesByReplacingUnderscoresWithSpaces() {
        verify(specFactory).makeSpec(Mockito.anyString(), Mockito.eq("only test"), Mockito.any(), Mockito.any(), Mockito.any());
      }

      @Test
      public void createsTheSpecFromTheItField() {
        verify(specFactory).makeSpec(Mockito.anyString(), Mockito.anyString(), argThat(fieldNamed("only_test")), Mockito.any(), Mockito.any());
      }

      @Test
      public void returnsASpecForThatField() {
        verify(specFactory).makeSpec(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        assertThat(returned, contains(sameInstance(toReturn)));
      }
    }

    public class givenAContextClassWithZeroOrOneOfEachFixtureField {
      @Before
      public void setup() {
        subject = new ClassSpecGateway(ContextClasses.FullFixture.class, specFactory);
        subject.getSpecs(subject.rootContext());
        verify(specFactory).makeSpec(Mockito.endsWith(".asserts"), Mockito.anyString(),
          Mockito.any(), befores.capture(), afters.capture());
      }

      @Test
      public void addsEstablishThenBecauseToEachSpecsBeforeFields() {
        assertThat(befores.getValue(), contains(fieldNamed("arranges"), fieldNamed("acts")));
      }

      @Test
      public void addsCleanupToEachSpecsAfterFields() {
        assertThat(afters.getValue(), contains(fieldNamed("cleans")));
      }
    }
  }

  private void givenSpecFactoryMakes(Spec spec) {
    when(specFactory.makeSpec(Mockito.anyString(), Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.any()))
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

  private void shouldHaveSpecs(Class<?> rootContextClass, long totalSpecs) {
    SpecGateway subject = new ClassSpecGateway(rootContextClass, specFactory);
    assertThat(subject.hasSpecs(), is(true));
    assertThat(subject.countSpecs(), equalTo(totalSpecs));
  }

  private void shouldNotHaveSpecs(Class<?> rootContextClass) {
    SpecGateway subject = new ClassSpecGateway(rootContextClass, specFactory);
    assertThat(subject.hasSpecs(), is(false));
    assertThat(subject.countSpecs(), equalTo(0L));
  }

  private void shouldNotReturnAnySpecs(Class<?> context) {
    subject = new ClassSpecGateway(context, specFactory);
    assertThat(subject.getSpecs(subject.rootContext()), hasSize(0));
  }

  private Matcher<Field> fieldNamed(String name) {
    return new BaseMatcher<Field>() {
      @Override
      public boolean matches(Object obj) {
        if(obj == null || obj.getClass() != Field.class)
          return false;

        Field field = (Field)obj;
        return name.equals(field.getName());
      }

      @Override
      public void describeTo(Description description) {
        description.appendText("Field declared with name ");
        description.appendValue(name);
      }
    };
  }
}
