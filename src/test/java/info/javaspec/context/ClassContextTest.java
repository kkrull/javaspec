package info.javaspec.context;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.junit.Descriptions;
import info.javaspec.spec.MockSpec;
import info.javaspec.spec.Spec;
import info.javaspecproto.ContextClasses;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runner.notification.RunNotifier;
import org.mockito.*;

import java.util.stream.Stream;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static info.javaspec.junit.DescriptionFactory.descriptionWithId;
import static info.javaspec.junit.Descriptions.isSuiteDescription;
import static info.javaspec.junit.Descriptions.isTestDescription;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

@RunWith(HierarchicalContextRunner.class)
public class ClassContextTest {
  private ClassContext subject;

  public class getDescription {
    private Description returned;

    public class givenNoSpecsOrSubContexts {
      @Test
      public void hasNoChildren() throws Exception {
        subject = ContextFactory.createRootContext(ContextClasses.Empty.class);
        returned = subject.getDescription();
        assertThat(returned.getChildren(), equalTo(newArrayList()));
      }
    }

    public class givenAContextClassWithSpecsOrSubContexts {
      @Before
      public void setup() throws Exception {
        subject = ContextFactory.createRootContext(ContextClasses.OneIt.class);
        returned = subject.getDescription();
      }

      @Test
      public void returnsASuiteDescription() throws Exception {
        assertThat(returned, isSuiteDescription());
      }

      @Test
      public void namesTheRootDescriptionWithTheSimpleNameOfTheRootContextClass() throws Exception {
        assertThat(returned.getDisplayName(), equalTo("OneIt"));
      }
    }

    public class givenAContextClassWithSpecs {
      @Test
      public void hasTestDescriptionsForEachSpecInTheContext() throws Exception {
        subject = ContextFactory.createRootContext(ContextClasses.TwoIts.class);
        returned = subject.getDescription();
        assertThat(Descriptions.childMethodNames(returned), equalTo(newHashSet("one", "two")));
        assertThat(returned.getChildren(), contains(isTestDescription(), isTestDescription()));
      }

      @Test
      public void setsTheDescriptionClassNameToThatOfTheParentContext() throws Exception {
        subject = ContextFactory.createRootContext(ContextClasses.UnderscoreSubContext.class);
        returned = subject.getDescription();
        assertThat(Descriptions.onlyTest(returned).getClassName(), equalTo("read me"));
      }

      @Test
      public void setsTheDescriptionMethodNameToTheHumanizedFieldName() throws Exception {
        subject = ContextFactory.createRootContext(ContextClasses.UnderscoreIt.class);
        returned = subject.getDescription();
        assertThat(Descriptions.childMethodNames(returned), equalTo(newHashSet("read me")));
      }

      //Two specs can have the same field name, if they are declared in different parts of the hierarchy.
      //This is a problem for test Descriptions if the identically-named fields are also in identically-named classes.
      @Test
      public void identifiesTestDescriptionsByTheFullyQualifiedFieldName() throws Exception {
        subject = ContextFactory.createRootContext(ContextClasses.DuplicateSpecNames.class);
        returned = subject.getDescription();

        Description leftLeaf = Descriptions.onlyTest(returned.getChildren().get(0));
        Description rightLeaf = Descriptions.onlyTest(returned.getChildren().get(1));
        assertThat(leftLeaf, not(equalTo(rightLeaf)));
      }
    }

    public class givenAContextClassWithSubContextClasses {
      @Test
      public void hasSuiteDescriptionsForEachSubContextClass() throws Exception {
        subject = ContextFactory.createRootContext(ContextClasses.TwoContexts.class);
        returned = subject.getDescription();
        assertThat(Descriptions.childClassNames(returned), equalTo(newHashSet("subcontext1", "subcontext2")));
        assertThat(returned.getChildren(), contains(isSuiteDescription(), isSuiteDescription()));
      }

      @Test
      public void humanizesSubContextClassNamesIntoHumanReadableClassNames_replacingUnderscoreWithSpace() throws Exception {
        subject = ContextFactory.createRootContext(ContextClasses.UnderscoreSubContext.class);
        returned = subject.getDescription();
        assertThat(Descriptions.childClassNames(returned), equalTo(newHashSet("read me")));
      }

      //Two context classes in different parts of the hierarchy can have the same simple name.
      //Make sure something unique is used for each Suite Description fUniqueId.
      @Test
      public void identifiesSuiteDescriptionsWithTheFullyQualifiedClassName() throws Exception {
        subject = ContextFactory.createRootContext(ContextClasses.DuplicateContextNames.class);
        returned = subject.getDescription();

        Description leftLeaf = Descriptions.onlyChild(returned.getChildren().get(0));
        Description rightLeaf = Descriptions.onlyChild(returned.getChildren().get(1));
        assertThat(leftLeaf, not(equalTo(rightLeaf)));
      }
    }
  }

  public class hasSpecs {
    @Test
    public void givenAClassWithoutAnySpecs_returns_false() throws Exception {
      subject = ContextFactory.createRootContext(ContextClasses.Empty.class);
      assertThat(subject.hasSpecs(), equalTo(false));
    }

    @Test
    public void givenAClassWithSpecs_returns_true() throws Exception {
      subject = ContextFactory.createRootContext(ContextClasses.OneIt.class);
      assertThat(subject.hasSpecs(), equalTo(true));
    }

    @Test
    public void givenAClassWhereASubContextHasSpecs_returns_true() throws Exception {
      subject = ContextFactory.createRootContext(ContextClasses.NestedIt.class);
      assertThat(subject.hasSpecs(), equalTo(true));
    }
  }

  public class numSpecs {
    @Test
    public void givenNoSpecsOrChildContexts_returns_0() throws Exception {
      subject = ContextFactory.createRootContext(ContextClasses.Empty.class);
      assertThat(subject.numSpecs(), equalTo(0L));
    }

    @Test
    public void givenAClassWith1OrMoreSpecs_countsThoseSpecs() throws Exception {
      subject = ContextFactory.createRootContext(ContextClasses.TwoIt.class);
      assertThat(subject.numSpecs(), equalTo(2L));
    }

    @Test
    public void givenAClassWithSubContexts_sumsSpecsInThoseClasses() throws Exception {
      subject = ContextFactory.createRootContext(ContextClasses.NestedContexts.class);
      assertThat(subject.numSpecs(), equalTo(2L));
    }
  }

  public class run {
    private final RunNotifier notifier = mock(RunNotifier.class);

    public class given1OrMoreSpecs {
      private final Spec firstChild = MockSpec.that().build();
      private final Spec secondChild = MockSpec.that().build();

      @Before
      public void setup() throws Exception {
        subject = classContextWithSpecs(firstChild, secondChild);
        subject.run(notifier);
      }

      @Test
      public void runsEachSpec() throws Exception {
        Mockito.verify(firstChild).run(notifier);
        Mockito.verify(secondChild).run(notifier);
      }
    }

    public class given1OrMoreSubContexts {
      @Test
      public void runsEachSubContext() throws Exception {
        Context firstChild = MockContext.anyValid();
        Context secondChild = MockContext.anyValid();
        subject = classContextWithSubContexts(firstChild, secondChild);

        subject.run(notifier);
        Mockito.verify(firstChild).run(notifier);
        Mockito.verify(secondChild).run(notifier);
      }
    }
  }

  @Test
  public void aSpecIs_anNonStaticItField() throws Exception {
    subject = ContextFactory.createRootContext(ContextClasses.StaticIt.class);
    assertThat(subject.numSpecs(), equalTo(0L));
  }

  @Test
  public void aSubContextIs_aNonStaticInnerClass() throws Exception {
    subject = ContextFactory.createRootContext(ContextClasses.NestedStaticClassIt.class);
    assertThat(subject.numSpecs(), equalTo(0L));
  }

  private static ClassContext classContextWithSpecs(Spec... specs) {
    ClassContext context = new ClassContext("classContextWithSpecs", descriptionWithId("classContextWithSpecs"));
    Stream.of(specs).forEach(context::addSpec);
    return context;
  }

  private static ClassContext classContextWithSubContexts(Context... subContexts) {
    ClassContext context = new ClassContext("classContextWithSubContexts", descriptionWithId("classContextWithSubContexts"));
    Stream.of(subContexts).forEach(context::addSubContext);
    return context;
  }
}
