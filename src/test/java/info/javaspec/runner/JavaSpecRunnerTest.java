package info.javaspec.runner;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.runner.JavaSpecRunner.NoSpecs;
import info.javaspec.runner.JavaSpecRunner.TooManySpecs;
import info.javaspec.testutil.RunListenerSpy;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static info.javaspec.testutil.Assertions.capture;
import static info.javaspec.testutil.Matchers.matchesRegex;
import static java.util.Collections.synchronizedList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assume.assumeThat;

@RunWith(HierarchicalContextRunner.class)
public class JavaSpecRunnerTest {
  @SuppressWarnings("unchecked") //Parameterized interface
  private final FakeSpecGateway gateway = new FakeSpecGateway();

  private Runner subject;

  public JavaSpecRunnerTest() {
    gateway.init(1, aLeafContext("NonEmptyContextForInitialization", aSpec("default_spec")));
    subject = new JavaSpecRunner(gateway);
  }

  public class constructor {
    @Test
    public void givenAClassWithoutAnySpecs_throwsNoSpecs() throws Exception {
      Context rootContext = FakeContext.withNoSpecs("ContextClasses$Empty", "Root context");
      Exception ex = capture(NoSpecs.class, () -> new JavaSpecRunner(rootContext));
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
    //This is a problem for test Descriptions if the identically-named fields are also in identically-named classes.
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
        assertThat(onlyElement(events).description, equalTo(onlyChild(subject.getDescription())));
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
        assertThat(events.get(0).description, equalTo(onlyChild(subject.getDescription())));
      }

      @Test
      public void runsTheSpec() {
        assertThat(gateway.rootContext.runCounts(), equalTo(newArrayList(1)));
      }

      @Test
      public void notifiesTestPassed() {
        assertThat(events.get(1).name, equalTo("testFinished"));
        assertThat(events.get(1).description, equalTo(onlyChild(subject.getDescription())));
      }

      @Test
      public void firesNoOtherEvents() {
        assertThat(events, hasSize(2));
      }
    }

    public class whenASpecFails {
      @Before
      public void setup() throws Exception {
        givenTheGatewayHasSpecs(1, aLeafContext("Root",
          aFailingSpec("Root::one_try_and_fail", new AssertionError("Tricksy AssertionError, like JUnit throws")),
          aFailingSpec("Root::another_try_and_fail", new RuntimeException("Unchecked exception"))
        ));
        subject.run(notifier);
      }

      @Test
      public void notifiesTestStarted() {
        Set<Description> started = run.this.events.stream()
          .filter(x -> "testStarted".equals(x.name))
          .map(x -> x.description)
          .collect(Collectors.toSet());

        shouldDescribeAllTests(started);
      }

      @Test
      public void notifiesTestFailed_withTheExceptionThatTriggeredFailure() {
        Set<Description> failedDescriptions = run.this.events.stream()
          .filter(x -> "testFailure".equals(x.name))
          .map(x -> x.description)
          .collect(Collectors.toSet());
        shouldDescribeAllTests(failedDescriptions);

        Set<Class<?>> failedCauses = run.this.events.stream()
          .filter(x -> "testFailure".equals(x.name))
          .map(x -> x.failure.getException())
          .map(x -> x.getClass())
          .collect(Collectors.toSet());
        assertThat(failedCauses, equalTo(newHashSet(RuntimeException.class, AssertionError.class)));
      }

      @Test
      public void firesNoOtherEvents() {
        assertThat(events, hasSize(4));
      }

      @Test
      public void runsRemainingSpecs_evenIfAnEarlierSpecFailed() {
        Stream<RunListenerSpy.Event> started = events.stream().filter(x -> "testStarted".equals(x.name));
        assertThat(started.collect(toList()), hasSize(2));
      }

      private void shouldDescribeAllTests(Set<Description> descriptions) {
        Description rootDescription = subject.getDescription();
        assertThat(descriptions, equalTo(new HashSet<>(rootDescription.getChildren())));
      }
    }
  }

  public class testCount {
    @Test
    public void givenAContextWithAnIntegerNumberOfSpecs_delegatesToTheRootContext() throws Exception {
      subject = new JavaSpecRunner(FakeContext.withSpecs(aSpec("one"), aSpec("two")));
      assertThat(subject.testCount(), equalTo(2));
    }

    @Test
    public void givenAContextWithMoreSpecsThanIntegers_throwsTooManySpecs() throws Exception {
      subject = new JavaSpecRunner(FakeContext.withNumSpecs("BigContext", (long)Integer.MAX_VALUE + 1));
      TooManySpecs ex = capture(TooManySpecs.class, subject::testCount);
      assertThat(ex.getMessage(), equalTo("Context BigContext has more specs than JUnit can support: 2147483648"));
    }
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

  private FakeSpec aFailingSpec(String id, AssertionError toThrow) {
    return new FakeSpec(id, id, false) {
      @Override
      public void run() { throw toThrow; }
    };
  }

  private FakeSpec aFailingSpec(String id, RuntimeException toThrow) {
    return new FakeSpec(id, id, false) {
      @Override
      public void run() { throw toThrow; }
    };
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
    public Stream<ClassContext> getSubcontexts(ClassContext context) {
      return asFakeContext(context).subcontexts.stream()
        .map(this::asClassContext);
    }

    @Override
    public boolean hasSpecs() { return numSpecs > 0; }

    @Override
    public long countSpecs() { return numSpecs; }

    @Override
    public Stream<Spec> getSpecs(ClassContext context) {
      FakeContext fakeContext = asFakeContext(context);
      return fakeContext.specs.stream().map(this::asSpec);
    }

    private FakeContext asFakeContext(Context context) { return (FakeContext)context; }
    private ClassContext asClassContext(FakeContext context) { return context; }
    private Spec asSpec(FakeSpec spec) { return spec; }
  }

  private static final class FakeContext extends ClassContext {
    public final List<FakeSpec> specs;
    public final List<FakeContext> subcontexts;
    private long numSpecs;

    public static Context withNumSpecs(String id, long numSpecs) {
      return new FakeContext(id, id, numSpecs);
    }

    public static FakeContext withSpecs(FakeSpec... specs) {
      return new FakeContext("root", "root", Arrays.asList(specs), new ArrayList<>(0));
    }

    public static FakeContext withNoSpecs(String id, String displayName) {
      return new FakeContext(id, displayName, new ArrayList<>(0), new ArrayList<>(0));
    }

    public FakeContext(String id, String displayName, List<FakeSpec> specs, List<FakeContext> subcontexts) {
      super(id, displayName, JavaSpecRunnerTest.class);
      this.specs = specs;
      this.subcontexts = subcontexts;
      this.numSpecs = this.specs.size();
    }

    public FakeContext(String id, String displayName, long numSpecs) {
      super(id, displayName, JavaSpecRunnerTest.class);
      this.specs = new ArrayList<>(0);
      this.subcontexts = new ArrayList<>(0);
      this.numSpecs = numSpecs;
    }

    public List<Integer> runCounts() {
      return specs.stream().map(x -> x.runCount).collect(toList());
    }

    @Override
    public boolean hasSpecs() { return numSpecs > 0; }

    @Override
    public long numSpecs() { return numSpecs; }
  }

  private static class FakeSpec extends Spec {
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