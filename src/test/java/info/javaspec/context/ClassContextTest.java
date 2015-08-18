package info.javaspec.context;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.runner.Descriptions;
import info.javaspec.spec.MockSpec;
import info.javaspec.spec.Spec;
import info.javaspecproto.ContextClasses;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.mockito.*;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static info.javaspec.runner.Descriptions.isSuiteDescription;
import static info.javaspec.runner.Descriptions.isTestDescription;
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
        subject = ClassContextFactory.createRootContext(ContextClasses.Empty.class);
        returned = subject.getDescription();
        assertThat(returned.getChildren(), equalTo(newArrayList()));
      }
    }

    public class givenAContextClassWithSpecsOrSubContexts {
      @Before
      public void setup() throws Exception {
        subject = ClassContextFactory.createRootContext(ContextClasses.OneIt.class);
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
        subject = ClassContextFactory.createRootContext(ContextClasses.TwoIts.class);
        returned = subject.getDescription();
        assertThat(Descriptions.childMethodNames(returned), equalTo(newHashSet("one", "two")));
        assertThat(returned.getChildren(), contains(isTestDescription(), isTestDescription()));
      }

      @Test
      public void setsTheDescriptionClassNameToThatOfTheParentContext() throws Exception {
        subject = ClassContextFactory.createRootContext(ContextClasses.UnderscoreSubContext.class);
        returned = subject.getDescription();
        assertThat(Descriptions.onlyTest(returned).getClassName(), equalTo("read me"));
      }

      @Test
      public void setsTheDescriptionMethodNameToTheHumanizedFieldName() throws Exception {
        subject = ClassContextFactory.createRootContext(ContextClasses.UnderscoreIt.class);
        returned = subject.getDescription();
        assertThat(Descriptions.childMethodNames(returned), equalTo(newHashSet("read me")));
      }

      //Two specs can have the same field name, if they are declared in different parts of the hierarchy.
      //This is a problem for test Descriptions if the identically-named fields are also in identically-named classes.
      @Test
      public void identifiesTestDescriptionsByTheFullyQualifiedFieldName() throws Exception {
        subject = ClassContextFactory.createRootContext(ContextClasses.DuplicateSpecNames.class);
        returned = subject.getDescription();

        Description leftLeaf = Descriptions.onlyTest(returned.getChildren().get(0));
        Description rightLeaf = Descriptions.onlyTest(returned.getChildren().get(1));
        assertThat(leftLeaf, not(equalTo(rightLeaf)));
      }
    }

    public class givenAContextClassWithSubContextClasses {
      @Test
      public void hasSuiteDescriptionsForEachSubContextClass() throws Exception {
        subject = ClassContextFactory.createRootContext(ContextClasses.TwoContexts.class);
        returned = subject.getDescription();
        assertThat(Descriptions.childClassNames(returned), equalTo(newHashSet("subcontext1", "subcontext2")));
        assertThat(returned.getChildren(), contains(isSuiteDescription(), isSuiteDescription()));
      }

      @Test
      public void humanizesSubContextClassNamesIntoHumanReadableClassNames_replacingUnderscoreWithSpace() throws Exception {
        subject = ClassContextFactory.createRootContext(ContextClasses.UnderscoreSubContext.class);
        returned = subject.getDescription();
        assertThat(Descriptions.childClassNames(returned), equalTo(newHashSet("read me")));
      }

      //Two context classes in different parts of the hierarchy can have the same simple name.
      //Make sure something unique is used for each Suite Description fUniqueId.
      @Test
      public void identifiesSuiteDescriptionsWithTheFullyQualifiedClassName() throws Exception {
        subject = ClassContextFactory.createRootContext(ContextClasses.DuplicateContextNames.class);
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
      subject = ClassContextFactory.createRootContext(ContextClasses.Empty.class);
      assertThat(subject.hasSpecs(), equalTo(false));
    }

    @Test
    public void givenAClassWithSpecs_returns_true() throws Exception {
      subject = ClassContextFactory.createRootContext(ContextClasses.OneIt.class);
      assertThat(subject.hasSpecs(), equalTo(true));
    }

    @Test
    public void givenAClassWhereASubContextHasSpecs_returns_true() throws Exception {
      subject = ClassContextFactory.createRootContext(ContextClasses.NestedIt.class);
      assertThat(subject.hasSpecs(), equalTo(true));
    }
  }

  public class numSpecs {
    @Test
    public void givenNoSpecsOrChildContexts_returns_0() throws Exception {
      subject = ClassContextFactory.createRootContext(ContextClasses.Empty.class);
      assertThat(subject.numSpecs(), equalTo(0L));
    }

    @Test
    public void givenAClassWith1OrMoreSpecs_countsThoseSpecs() throws Exception {
      subject = ClassContextFactory.createRootContext(ContextClasses.TwoIt.class);
      assertThat(subject.numSpecs(), equalTo(2L));
    }

    @Test
    public void givenAClassWithSubContexts_sumsSpecsInThoseClasses() throws Exception {
      subject = ClassContextFactory.createRootContext(ContextClasses.NestedContexts.class);
      assertThat(subject.numSpecs(), equalTo(2L));
    }
  }

  public class run {
    private final RunNotifier notifier = mock(RunNotifier.class);

    public class given1OrMoreSpecs {
      private final Description firstDescription = DescriptionFactory.descriptionWithId("1");
      private final Spec firstChild = MockSpec.that().hasDescription(firstDescription).build();
      private final Description secondDescription = DescriptionFactory.descriptionWithId("2");
      private final Spec secondChild = MockSpec.that().hasDescription(secondDescription).build();

      @Before
      public void setup() throws Exception {
        subject = classContextWithSpecs(firstChild, secondChild);
        subject.run(notifier);
      }

      @Test
      public void runsEachSpec() throws Exception {
        Mockito.verify(firstChild).run();
        Mockito.verify(secondChild).run();
      }

      @Test
      public void notifiesWhenStartingAndEndingEachTest() throws Exception {
        InOrder sequence = Mockito.inOrder(notifier, firstChild, secondChild);
        sequence.verify(notifier).fireTestStarted(Mockito.same(firstDescription));
        sequence.verify(firstChild).run();
        sequence.verify(notifier).fireTestFinished(Mockito.same(firstDescription));
        sequence.verify(notifier).fireTestStarted(Mockito.same(secondDescription));
        sequence.verify(secondChild).run();
        sequence.verify(notifier).fireTestFinished(Mockito.same(secondDescription));
        sequence.verifyNoMoreInteractions();
      }
    }

    public class given2OrMoreSpecs {
      public class whenOneSpecThrows {
        private final Spec firstSpec = MockSpec.that().diesWhenRun().build();
        private final Spec secondSpec = MockSpec.that().diesWhenRun().build();

        @Before
        public void setup() throws Exception {
          subject = classContextWithSpecs(firstSpec, secondSpec);
          subject.run(notifier);
        }

        @Test
        public void runsAllRemainingSpecs() throws Exception {
          Mockito.verify(firstSpec).run();
          Mockito.verify(secondSpec).run();
        }
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

    public class givenAContextWithAnEstablishField {
      @Test @Ignore
      public void createsSpecsWithThatAsABeforeEachFixtureLambda() throws Exception {
        //Option 1: Create a real spec with real fields that have observable side effects.
        //- meant to test: Call to FieldSpec#new
        //- also depends on: Working FieldSpec#run

        //Option 2: Extract ClassContext#create methods into ContextFactory.  DI factory method (FieldSpec#new).

        //Option 3: DI factory method (FieldSpec#new) into ClassContext#create methods.
        assertThat("pending", equalTo("passing"));
      }
    }

    public class givenAContextWithABecauseField {
      @Test @Ignore
      public void createsSpecsWithThatAsABeforeEachFixtureLambda() throws Exception {}
    }

    public class givenAContextWithACleanupField {
      @Test @Ignore
      public void createsSpecsWithThatAsAnAfterEachFixtureLambda() throws Exception {}
    }

    public class whenASpecPasses {
      private final Description description = DescriptionFactory.descriptionWithId("passes");
      private final Spec spec = MockSpec.that().hasDescription(description).build();

      @Before
      public void setup() throws Exception {
        subject = classContextWithSpecs(spec);
        subject.run(notifier);
      }

      @Test
      public void notifiesTestSuccess() throws Exception {
        InOrder sequence = Mockito.inOrder(notifier, spec);
        sequence.verify(notifier).fireTestStarted(Mockito.any(Description.class));
        sequence.verify(spec).run();
        sequence.verify(notifier).fireTestFinished(Mockito.eq(description));
        sequence.verifyNoMoreInteractions();
      }
    }

    public class whenASpecThrowsTestSetupFailed {
      @Test @Ignore
      public void notifiesTestAssumptionFailed() throws Exception {}
    }

    public class whenASpecThrowsAnythingElse {
      private @Captor ArgumentCaptor<Failure> failureCaptor;

      private final Description description = DescriptionFactory.descriptionWithId("failing");
      private final Spec spec = MockSpec.that()
        .hasDescription(description)
        .diesWhenRun(new AssertionError("sufferin' succotash")) //TODO KDK: Catch whatever JUnit's Assert throws
        .build();

      @Before
      public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        subject = classContextWithSpecs(spec);
        subject.run(notifier);
      }

      @Test
      public void notifiesTestFailureWithTheTestDescriptionAndThrownException() throws Exception {
        InOrder sequence = Mockito.inOrder(notifier, spec);
        sequence.verify(notifier).fireTestStarted(Mockito.any(Description.class));
        sequence.verify(spec).run();
        sequence.verify(notifier).fireTestFailure(failureCaptor.capture());
        sequence.verifyNoMoreInteractions();

        Failure failure = failureCaptor.getValue();
        assertThat(failure.getDescription(), sameInstance(description));
        assertThat(failure.getException().getMessage(), equalTo("sufferin' succotash"));
      }
    }
  }

  @Test
  public void aSpecIs_anNonStaticItField() throws Exception {
    subject = ClassContextFactory.createRootContext(ContextClasses.StaticIt.class);
    assertThat(subject.numSpecs(), equalTo(0L));
  }

  @Test
  public void aSubContextIs_aNonStaticInnerClass() throws Exception {
    subject = ClassContextFactory.createRootContext(ContextClasses.NestedStaticClassIt.class);
    assertThat(subject.numSpecs(), equalTo(0L));
  }

  private static ClassContext classContextWithSpecs(Spec... specs) {
    return new ClassContext("classContextWithSpecs", "classContextWithSpecs", newArrayList(specs), newArrayList());
  }

  private static ClassContext classContextWithSubContexts(Context... subContexts) {
    return new ClassContext("classContextWithSubContexts", "classContextWithSubContexts", newArrayList(), newArrayList(subContexts));
  }
}
