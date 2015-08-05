package info.javaspec.runner;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspecproto.ContextClasses;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runner.notification.RunNotifier;
import org.mockito.Mockito;

import javax.management.Descriptor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

@RunWith(HierarchicalContextRunner.class)
public class ClassContextTest {
  private ClassContext subject;

  public class getDescription_reference {
    public class givenAContextHierarchy {
      @Test public void nop() throws Exception { }
//      @Before
//      public void setup() {
//        gateway.init(1,
//          aNestedContext("Root",
//            aNestedContext("Middle",
//              aLeafContext("Bottom", aSpec("one"))))
//        );
//        description = subject.getDescription();
//      }
//
//      @Test
//      public void createsASuiteHierarchyMatchingTheContextHierarchy() throws Exception {
//        assertThat(description, isASuiteDescription("Root"));
//        assertThat(onlyChild(description), isASuiteDescription("Middle"));
//        assertThat(onlyChild(onlyChild(description)), isASuiteDescription("Bottom"));
//      }
    }

    public class givenAContext {
      @Test public void nop() throws Exception { }
//      @Before
//      public void setup() throws Exception {
//        givenTheGatewayHasSpecs(1, aLeafContext("RootId", "RootDisplay", aSpec("one"), aSpec("two")));
//        description = subject.getDescription();
//      }
//
//      @Test
//      public void createsASuiteDescriptionForThatContext_whereTheSuiteClassNameIsTheContextDisplayName() {
//        assertThat(description, isASuiteDescription("RootDisplay"));
//      }
//
//      @Test
//      public void addsATestDescriptionForEachSpecInTheContext() throws Exception {
//        assertThat(description.getChildren(), hasSize(2));
//      }
    }

    public class givenASpec {
      @Test public void nop() throws Exception { }
//      @Before
//      public void setup() throws Exception {
//        givenTheGatewayHasSpecs(1, aLeafContext("RootId", "RootDisplay", aSpec("oneId", "oneDisplay")));
//        description = onlyChild(subject.getDescription());
//      }
//
//      @Test
//      public void createsATestDescriptionForThatContext() {
//        assertThat(description, isATestDescription());
//      }
//
//      @Test
//      public void usesTheContextDisplayNameForTheTestClassName() {
//        assertThat(description.getClassName(), equalTo("RootDisplay"));
//      }
//
//      @Test
//      public void usesTheSpecDisplayNameForTheTestMethodName() {
//        assertThat(description.getMethodName(), equalTo("oneDisplay"));
//      }
    }

    //Two context classes in different parts of the hierarchy can have the same simple name.
    //Make sure something unique is used for each Suite Description uniqueId.
    public class givenAContextHierarchyWith2OrMoreInstancesOfTheSameDisplayName {
      @Test public void nop() throws Exception { }
//      @Before
//      public void setup() {
//        FakeContext leftDuplicate = aLeafContext("Root$LeftSuite$SameClassName", "SameClassName", aSpec("one"));
//        FakeContext rightDuplicate = aLeafContext("Root$RightSuite$SameClassName", "SameClassName", aSpec("one"));
//        assumeThat(leftDuplicate.displayName, equalTo(rightDuplicate.displayName)); //Basis for Description.fUniqueId
//
//        gateway.init(2, aNestedContext("Root",
//            aNestedContext("LeftSuite", leftDuplicate),
//            aNestedContext("RightSuite", rightDuplicate))
//        );
//        description = subject.getDescription();
//      }
//
//      @Test
//      public void theSuiteDescriptionsAreNotEqual() {
//        Description leftFork = description.getChildren().get(0);
//        Description one = onlyChild(leftFork);
//
//        Description rightFork = description.getChildren().get(1);
//        Description other = onlyChild(rightFork);
//        assertThat(one, not(equalTo(other)));
//      }
    }

    //Two specs can have the same name too, if they are declared in different parts of the hierarchy.
    //This is a problem for test Descriptions if the identically-named fields are also in identically-named classes.
    public class givenAContextHierarchyWith2OrMoreSpecsOfTheSameDisplayName {
      @Test public void nop() throws Exception { }
//      @Before
//      public void setup() {
//        FakeSpec leftSpec = aSpec("LeftSuite.same_name", "same name");
//        FakeSpec rightSpec = aSpec("RightSuite.same_name", "same name");
//        assumeThat(leftSpec.displayName, equalTo(rightSpec.displayName)); //Part of Description.fUniqueId
//
//        FakeContext leftDuplicate = aLeafContext("Root$LeftSuite$SameClassName", "SameClassName", leftSpec);
//        FakeContext rightDuplicate = aLeafContext("Root$RightSuite$SameClassName", "SameClassName", rightSpec);
//        assumeThat(leftDuplicate.displayName, equalTo(rightDuplicate.displayName)); //Part of Description.fUniqueId
//
//        gateway.init(2, aNestedContext("Root",
//            aNestedContext("LeftSuite", leftDuplicate),
//            aNestedContext("RightSuite", rightDuplicate))
//        );
//        description = subject.getDescription();
//      }
//
//      @Test
//      public void theTestDescriptionsAreNotEqual() {
//        Description leftFork = onlyChild(description.getChildren().get(0));
//        Description one = onlyChild(leftFork);
//
//        Description rightFork = onlyChild(description.getChildren().get(1));
//        Description other = onlyChild(rightFork);
//        assertThat(one, not(equalTo(other)));
//      }
    }
  }

  public class getDescription {
    private Description returned;

    public class givenAContextClass {
      @Before
      public void setup() throws Exception {
        subject = AClassContext.of(ContextClasses.OneIt.class);
        returned = subject.getDescription();
      }

      @Test @Ignore
      public void returnsASuiteDescription() throws Exception {
        assertThat(returned, DescriptionMatchers.isSuiteDescription());
      }

      @Test
      public void namesTheRootDescriptionWithTheSimpleNameOfTheRootContextClass() throws Exception {
        assertThat(returned.getDisplayName(), equalTo("OneIt"));
      }

      @Test
      public void hasTestDescriptionsForEachSpecInTheContext() throws Exception {
        assertThat(Descriptions.childDisplayNames(returned), equalTo(newHashSet("only_test")));
      }
    }

    public class givenNoSpecsOrSubContexts {
      @Test
      public void returnsASuiteWithNoChildren() throws Exception {
        subject = AClassContext.of(ContextClasses.Empty.class);
        returned = subject.getDescription();
        assertThat(returned.getChildren(), equalTo(newArrayList()));
      }
    }
  }

  public class hasSpecs {
    @Test
    public void givenAClassWithoutAnySpecs_returns_false() throws Exception {
      subject = AClassContext.of(ContextClasses.Empty.class);
      assertThat(subject.hasSpecs(), equalTo(false));
    }

    @Test
    public void givenAClassWithSpecs_returns_true() throws Exception {
      subject = AClassContext.of(ContextClasses.OneIt.class);
      assertThat(subject.hasSpecs(), equalTo(true));
    }

    @Test
    public void givenAClassWhereASubContextHasSpecs_returns_true() throws Exception {
      subject = AClassContext.of(ContextClasses.NestedIt.class);
      assertThat(subject.hasSpecs(), equalTo(true));
    }
  }

  public class numSpecs {
    @Test
    public void givenNoSpecsOrChildContexts_returns_0() throws Exception {
      subject = AClassContext.of(ContextClasses.Empty.class);
      assertThat(subject.numSpecs(), equalTo(0L));
    }

    @Test
    public void givenAClassWith1OrMoreSpecs_countsThoseSpecs() throws Exception {
      subject = AClassContext.of(ContextClasses.TwoIt.class);
      assertThat(subject.numSpecs(), equalTo(2L));
    }

    @Test
    public void givenAClassWithSubContexts_sumsSpecsInThoseClasses() throws Exception {
      subject = AClassContext.of(ContextClasses.NestedContexts.class);
      assertThat(subject.numSpecs(), equalTo(2L));
    }
  }

  public class run {
    private final RunNotifier notifier = mock(RunNotifier.class);

    public class given1OrMoreSpecs {
      @Test
      public void runsEachSpec() throws Exception {
        Spec firstChild = MockSpec.anyValid();
        Spec secondChild = MockSpec.anyValid();
        subject = AClassContext.withSpecs(firstChild, secondChild);

        subject.run(notifier);
        Mockito.verify(firstChild).run(notifier);
        Mockito.verify(secondChild).run(notifier);
      }
    }

    public class given1OrMoreSubContexts {
      @Test
      public void runsEachSubContext() throws Exception {
        Context firstChild = info.javaspec.runner.MockContext.anyValid();
        Context secondChild = info.javaspec.runner.MockContext.anyValid();
        subject = AClassContext.withSubContexts(firstChild, secondChild);

        subject.run(notifier);
        Mockito.verify(firstChild).run(notifier);
        Mockito.verify(secondChild).run(notifier);
      }
    }
  }

  @Test
  public void aSpecIs_anNonStaticItField() throws Exception {
    subject = AClassContext.of(ContextClasses.StaticIt.class);
    assertThat(subject.numSpecs(), equalTo(0L));
  }

  @Test
  public void aSubContextIs_aNonStaticInnerClass() throws Exception {
    subject = AClassContext.of(ContextClasses.NestedStaticClassIt.class);
    assertThat(subject.numSpecs(), equalTo(0L));
  }

  private static final class AClassContext {
    public static ClassContext of(Class<?> source) {
      return ClassContext.create(source);
    }

    public static ClassContext withSpecs(Spec... specs) {
      return new ClassContext("withSpecs", newArrayList(specs), newArrayList());
    }

    public static ClassContext withSubContexts(Context... subContexts) {
      return new ClassContext("withSubContexts", newArrayList(), newArrayList(subContexts));
    }
  }

  private static final class Descriptions {
    private Descriptions() { /* static class */ }

    public static Set<String> childDisplayNames(Description description) {
      return description.getChildren().stream().map(Description::getDisplayName).collect(toSet());
    }
  }

  private static final class DescriptionMatchers {
    private DescriptionMatchers() { /* static class */ }

    public static Matcher<? super Description> hasChildrenNamed(String... names) {
      final Set<String> expectedNames = Stream.of(names).collect(toSet());

      return new BaseMatcher<Description>() {
        @Override
        public boolean matches(Object item) {
          if(item.getClass() != Description.class)
            return false;

          Description description = Description.class.cast(item);
          Set<String> actualNames = description.getChildren().stream().map(Description::getDisplayName).collect(toSet());
          return expectedNames.equals(actualNames);
        }

        @Override
        public void describeTo(org.hamcrest.Description description) {
          description.appendText("a description with children named ");
          description.appendValueList("[", ",", "]", expectedNames);
        }
      };
    }

    public static Matcher<? super Description> isSuiteDescription() {
      return new BaseMatcher<Description>() {
        @Override
        public boolean matches(Object item) {
          if(item.getClass() != Description.class)
            return false;

          Description description = Description.class.cast(item);
          return description.isSuite() && !description.isTest();
        }

        @Override
        public void describeTo(org.hamcrest.Description description) {
          description.appendText("a suite description");
        }
      };
    }

    public static Matcher<? super Description> isTestDescription() {
      return new BaseMatcher<Description>() {
        @Override
        public boolean matches(Object item) {
          if(item.getClass() != Description.class)
            return false;

          Description description = Description.class.cast(item);
          return !description.isSuite() && description.isTest();
        }

        @Override
        public void describeTo(org.hamcrest.Description description) {
          description.appendText("a test description");
        }
      };
    }
  }
}
