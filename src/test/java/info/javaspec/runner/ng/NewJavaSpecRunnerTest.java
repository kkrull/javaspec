package info.javaspec.runner.ng;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.runner.ng.NewJavaSpecRunner.NoSpecs;
import info.javaspec.runner.ng.NewJavaSpecRunner.TooManySpecs;
import info.javaspec.testutil.RunListenerSpy;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import static com.google.common.collect.Lists.newArrayList;
import static info.javaspec.testutil.Assertions.capture;
import static info.javaspec.testutil.Matchers.matchesRegex;
import static java.util.Collections.synchronizedList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assume.assumeThat;

@RunWith(HierarchicalContextRunner.class)
public class NewJavaSpecRunnerTest {
  @SuppressWarnings("unchecked") //Parameterized interface
  private final FakeSpecGateway gateway = new FakeSpecGateway();

  private final Runner subject;

  public NewJavaSpecRunnerTest() {
    gateway.init(1, aLeafContext("NonEmptyContextForInitialization", aSpec("default_spec")));
    subject = new NewJavaSpecRunner(gateway);
  }

  public class constructor {
    @Test
    public void givenAClassWithoutAnySpecs_throwsNoSpecs() throws Exception {
      givenTheGatewayHasNoSpecs("ContextClasses$Empty");
      Exception ex = capture(NoSpecs.class, () -> new NewJavaSpecRunner(gateway));
      assertThat(ex.getMessage(), matchesRegex("^Context ContextClasses[$]Empty must contain at least 1 spec"));
    }
  }

  public class getDescription {
    private Description description;

    public class givenAContextHierarchy {
      @Before
      public void setup() {
        gateway.init(1,
          aNestedContext("Root",
            aNestedContext("Middle",
              aLeafContext("Bottom", aSpec("one"))))
        );
        description = subject.getDescription();
      }

      @Test
      public void createsASuiteHierarchyMatchingTheContextHierarchy() throws Exception {
        assertThat(description, isASuiteDescription("Root"));
        assertThat(onlyChild(description), isASuiteDescription("Middle"));
        assertThat(onlyChild(onlyChild(description)), isASuiteDescription("Bottom"));
      }
    }

    public class givenAContext {
      @Before
      public void setup() throws Exception {
        givenTheGatewayHasSpecs(1, aLeafContext("RootId", "RootDisplay", aSpec("one"), aSpec("two")));
        description = subject.getDescription();
      }

      @Test
      public void createsASuiteDescriptionForThatContext_whereTheSuiteClassNameIsTheContextDisplayName() {
        assertThat(description, isASuiteDescription("RootDisplay"));
      }

      @Test
      public void addsATestDescriptionForEachSpecInTheContext() throws Exception {
        assertThat(description.getChildren(), hasSize(2));
      }
    }

    public class givenASpec {
      @Before
      public void setup() throws Exception {
        givenTheGatewayHasSpecs(1, aLeafContext("RootId", "RootDisplay", aSpec("oneId", "oneDisplay")));
        description = onlyChild(subject.getDescription());
      }

      @Test
      public void createsATestDescriptionForThatContext() {
        assertThat(description, isATestDescription());
      }

      @Test
      public void usesTheContextDisplayNameForTheTestClassName() {
        assertThat(description.getClassName(), equalTo("RootDisplay"));
      }

      @Test
      public void usesTheSpecDisplayNameForTheTestMethodName() {
        assertThat(description.getMethodName(), equalTo("oneDisplay"));
      }
    }

    //Two context classes in different parts of the hierarchy can have the same simple name.
    //Make sure something unique is used for each Suite Description uniqueId.
    public class givenAContextHierarchyWith2OrMoreInstancesOfTheSameDisplayName {
      @Before
      public void setup() {
        FakeContext leftDuplicate = aLeafContext("Root$LeftSuite$SameClassName", "SameClassName", aSpec("one"));
        FakeContext rightDuplicate = aLeafContext("Root$RightSuite$SameClassName", "SameClassName", aSpec("one"));
        assumeThat(leftDuplicate.displayName, equalTo(rightDuplicate.displayName)); //Basis for Description.fUniqueId

        gateway.init(2, aNestedContext("Root",
          aNestedContext("LeftSuite", leftDuplicate),
          aNestedContext("RightSuite", rightDuplicate))
        );
        description = subject.getDescription();
      }

      @Test
      public void theSuiteDescriptionsAreNotEqual() {
        Description leftFork = description.getChildren().get(0);
        Description one = onlyChild(leftFork);

        Description rightFork = description.getChildren().get(1);
        Description other = onlyChild(rightFork);
        assertThat(one, not(equalTo(other)));
      }
    }

    //Two specs can have the same name too, if they are declared in different parts of the hierarchy.
    //A Test Description by default will use its class and method names to form a uniqueId, but even this combination
    //may not be unique if the display name for the specs and their respective contexts are the same.
    public class givenAContextHierarchyWith2OrMoreSpecsOfTheSameDisplayName {
      @Before
      public void setup() {
        FakeSpec leftSpec = aSpec("LeftSuite.same_name", "same name");
        FakeSpec rightSpec = aSpec("RightSuite.same_name", "same name");
        assumeThat(leftSpec.displayName, equalTo(rightSpec.displayName)); //Part of Description.fUniqueId

        FakeContext leftDuplicate = aLeafContext("Root$LeftSuite$SameClassName", "SameClassName", leftSpec);
        FakeContext rightDuplicate = aLeafContext("Root$RightSuite$SameClassName", "SameClassName", rightSpec);
        assumeThat(leftDuplicate.displayName, equalTo(rightDuplicate.displayName)); //Part of Description.fUniqueId

        gateway.init(2, aNestedContext("Root",
          aNestedContext("LeftSuite", leftDuplicate),
          aNestedContext("RightSuite", rightDuplicate))
        );
        description = subject.getDescription();
      }

      @Test
      public void theTestDescriptionsAreNotEqual() {
        Description leftFork = onlyChild(description.getChildren().get(0));
        Description one = onlyChild(leftFork);

        Description rightFork = onlyChild(description.getChildren().get(1));
        Description other = onlyChild(rightFork);
        assertThat(one, not(equalTo(other)));
      }
    }
  }

  public class run {
    final List<RunListenerSpy.Event> events = synchronizedList(new LinkedList<>());
    private final RunListenerSpy listener = new RunListenerSpy(events::add);
    private final RunNotifier notifier = new RunNotifier();

    @Before
    public void setup() throws Exception {
      notifier.addListener(listener);
    }

    public class givenAContextWith1OrMoreSpecs {
      @Before
      public void setup() throws Exception {
        givenTheGatewayHasSpecs(1, aLeafContext("Root",
          aSpec("Root::one", "one"),
          aSpec("Root::two", "two")
        ));
        subject.run(notifier);
      }

      @Test
      public void runsEachSpecInTheContext() {
        assertThat(gateway.rootContext.runCounts(), equalTo(newArrayList(1, 1)));
      }
    }

    public class givenSpecsIn1OrMoreContexts {
      @Before
      public void setup() throws Exception {
        givenTheGatewayHasSpecs(1, aNestedContext("Root",
          aLeafContext("Left", aSpec("Left::one")),
          aLeafContext("Right", aSpec("Right::one"))
        ));
        subject.run(notifier);
      }

      @Test
      public void runsSpecsInEachContext() {
        List<Integer> runCounts = gateway.rootContext.subcontexts.stream()
          .flatMap(x -> x.runCounts().stream())
          .collect(toList());
        assertThat(runCounts, equalTo(newArrayList(1, 1)));
      }
    }

    public class givenASpec {
      private Description suiteDescription;

      @Before
      public void setup() throws Exception {
        givenTheGatewayHasSpecs(1, aLeafContext("Root", aSpec("Root::one", "one")));
        suiteDescription = subject.getDescription();
        subject.run(notifier);
      }

      @Test
      public void usesAnEquivalentDescriptionToTheOneReturnedFrom_getDescription() {
        assertThat(suiteDescription.getChildren().get(0), equalTo(events.get(0).description));
      }

      //Technically, it's overwriting Description objects with equivalents in its internal cache.
      //However, this shouldn't be an issue because the context/specs are assumed to be fixed by the time it gets here
      @Test @Ignore
      public void whenDescribingAgain_clearsSpecDescriptionCache() {}
    }

    public class givenAnIgnoredSpec {
      @Before
      public void setup() throws Exception {
        givenTheGatewayHasSpecs(1, aLeafContext("Root", anIgnoredSpec("Root::ignore_me")));
        subject.run(notifier);
      }

      @Test
      public void notifiesIgnored() {
        assertThat(onlyElement(events).name, equalTo("testIgnored"));
      }

      @Test
      public void doesNotRunTheSpec() {
        assertThat(gateway.rootContext.runCounts(), equalTo(newArrayList(0)));
      }

      @Test
      public void firesNoOtherEvents() {
        assertThat(events, hasSize(1));
      }
    }

    public class whenASpecPasses {
      @Before
      public void setup() throws Exception {
        givenTheGatewayHasSpecs(1, aLeafContext("Root", aSpec("Root::passes")));
        subject.run(notifier);
      }

      @Test
      public void notifiesTestStarted() {
        assertThat(events.get(0).name, equalTo("testStarted"));
      }

      @Test
      public void runsTheSpec() {
        assertThat(gateway.rootContext.runCounts(), equalTo(newArrayList(1)));
      }

      @Test
      public void notifiesTestPassed() {
        assertThat(events.get(1).name, equalTo("testFinished"));
      }

      @Test
      public void firesNoOtherEvents() {
        assertThat(events, hasSize(2));
      }
    }

    public class whenASpecFails {
      @Test @Ignore
      public void notifiesTestStarted() {}

      @Test @Ignore
      public void notifiesTestFailed_withTheExceptionThatTriggeredFailure() {}

      @Test @Ignore
      public void firesNoOtherEvents() {
        assertThat(events, hasSize(2));
      }

      @Test @Ignore
      public void continuesRunningOtherSpecs() {}
    }
  }

  public class testCount {
    public class givenAClassWith1OrMoreSpecs {
      @Test
      public void returnsTheNumberOfTestsInTheGivenContextClass() throws Exception {
        givenTheGatewayHasSpecs(2, aLeafContext("Root", aSpec("one"), aSpec("two")));
        assertThat(subject.testCount(), equalTo(2));
      }
    }

    public class givenAClassWithMoreSpecsThanThereAreIntegers {
      @Test
      public void throwsTooManyTests() throws Exception {
        givenTheGatewayHasAnEnormousNumberOfSpecs("BigContext");
        TooManySpecs ex = capture(TooManySpecs.class, () -> new NewJavaSpecRunner(gateway).testCount());
        assertThat(ex.getMessage(), equalTo("Context BigContext has more specs than JUnit can support: 2147483648"));
      }
    }
  }

  private void givenTheGatewayHasNoSpecs(String rootContextId) {
    gateway.init(0, aLeafContext(rootContextId));
  }

  private void givenTheGatewayHasAnEnormousNumberOfSpecs(String rootContextId) {
    long numSpecs = (long) (Integer.MAX_VALUE) + 1;
    gateway.init(numSpecs, aLeafContext(rootContextId));
  }

  private void givenTheGatewayHasSpecs(long numSpecs, FakeContext rootContext) {
    gateway.init(numSpecs, rootContext);
  }

  private static Matcher<Description> isATestDescription() {
    return new BaseMatcher<Description>() {
      @Override
      public boolean matches(Object o) {
        if(o == null || o.getClass() != Description.class)
          return false;

        Description other = (Description)o;
        return other.isTest() && !other.isSuite();
      }

      @Override
      public void describeTo(org.hamcrest.Description description) { description.appendText("a test Description"); }
    };
  }

  private static Matcher<Description> isASuiteDescription(String className) {
    return new BaseMatcher<Description>() {
      @Override
      public boolean matches(Object o) {
        if(o == null || o.getClass() != Description.class)
          return false;

        Description other = (Description)o;
        return !other.isTest() && other.isSuite()
          && className.equals(other.getClassName());
      }

      @Override
      public void describeTo(org.hamcrest.Description description) {
        description.appendText("a suite Description named ");
        description.appendValue(className);
      }
    };
  }

  private Description onlyChild(Description suite) { return onlyElement(suite.getChildren()); }

  private <E> E onlyElement(List<E> items) {
    if(items.size() != 1) {
      String msg = String.format("Expected %s to have 1 element, but has %d", items, items.size());
      throw new RuntimeException(msg);
    }

    return items.get(0);
  }

  private FakeContext aNestedContext(String id, FakeContext... subcontexts) {
    return new FakeContext(id, id, new ArrayList<>(0), Arrays.asList(subcontexts));
  }

  private FakeContext aLeafContext(String id, FakeSpec... specs) {
    return new FakeContext(id, id, Arrays.asList(specs), new ArrayList<>(0));
  }

  private FakeContext aLeafContext(String id, String displayName, FakeSpec... specs) {
    return new FakeContext(id, displayName, Arrays.asList(specs), new ArrayList<>(0));
  }

  private FakeSpec aSpec(String id) {
    return new FakeSpec(id, id, false);
  }

  private FakeSpec aSpec(String id, String displayName) {
    return new FakeSpec(id, displayName, false);
  }

  private FakeSpec anIgnoredSpec(String id) {
    return new FakeSpec(id, id, true);
  }

  private static final class FakeSpecGateway implements SpecGateway<ClassContext> {
    private FakeContext rootContext;
    private long numSpecs;

    public void init(long numSpecs, FakeContext rootContext) {
      this.rootContext = rootContext;
      this.numSpecs = numSpecs;
    }

    @Override
    public FakeContext rootContext() { return rootContext; }

    @Override
    public String rootContextId() { return rootContext.id; }

    @Override
    public List<ClassContext> getSubcontexts(ClassContext context) {
      return asFakeContext(context).subcontexts.stream()
        .map(this::asClassContext)
        .collect(toList());
    }

    @Override
    public boolean hasSpecs() { return numSpecs > 0; }

    @Override
    public long countSpecs() { return numSpecs; }

    @Override
    public List<Spec> getSpecs(ClassContext context) {
      FakeContext fakeContext = asFakeContext(context);
      return fakeContext.specs.stream()
        .map(this::asSpec)
        .collect(toList());
    }

    private FakeContext asFakeContext(Context context) { return (FakeContext)context; }
    private ClassContext asClassContext(FakeContext context) { return context; }
    private Spec asSpec(FakeSpec spec) { return spec; }
  }

  private static final class FakeContext extends ClassContext {
    public final List<FakeSpec> specs;
    public final List<FakeContext> subcontexts;

    public FakeContext(String id, String displayName, List<FakeSpec> specs, List<FakeContext> subcontexts) {
      super(id, displayName, NewJavaSpecRunnerTest.class);
      this.specs = specs;
      this.subcontexts = subcontexts;
    }

    public List<Integer> runCounts() {
      return specs.stream().map(x -> x.runCount).collect(toList());
    }
  }

  private static final class FakeSpec extends Spec {
    private final boolean isIgnored;
    public int runCount;

    public FakeSpec(String id, String displayName, boolean isIgnored) {
      super(id, displayName);
      this.isIgnored = isIgnored;
    }

    @Override
    public boolean isIgnored() { return isIgnored; }

    @Override
    public void run() { runCount++; }
  }
}