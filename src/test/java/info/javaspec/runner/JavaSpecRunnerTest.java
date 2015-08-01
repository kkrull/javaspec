package info.javaspec.runner;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.runner.JavaSpecRunner.NoSpecs;
import info.javaspec.runner.JavaSpecRunner.TooManySpecs;
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
import org.mockito.Mockito;

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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(HierarchicalContextRunner.class)
public class JavaSpecRunnerTest {
  @SuppressWarnings("unchecked") //Parameterized interface
  private final FakeSpecGateway gateway = new FakeSpecGateway();

  private Runner subject;

  public JavaSpecRunnerTest() {
    gateway.init(1, FakeContext.leaf("NonEmptyContextForInitialization", FakeSpec.with("default_spec")));
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
    @Test
    public void delegatesToTheRootContext() throws Exception {
      Description description = Description.createSuiteDescription("Root", Long.valueOf(1L));
      subject = new JavaSpecRunner(FakeContext.withDescription(description));
      assertThat(subject.getDescription(), sameInstance(description));
    }
  }

  public class run {
    @Test
    public void delegatesToTheRootContext() {
      Context rootContext = FakeContext.anyValid();
      subject = new JavaSpecRunner(rootContext);
      subject.run(mock(RunNotifier.class));
      verify(rootContext).run(Mockito.any());
    }
  }

  public class testCount {
    @Test
    public void givenAContextWithAnIntegerNumberOfSpecs_delegatesToTheRootContext() throws Exception {
      subject = new JavaSpecRunner(FakeContext.withSpecs(FakeSpec.with("one"), FakeSpec.with("two")));
      assertThat(subject.testCount(), equalTo(2));
    }

    @Test
    public void givenAContextWithMoreSpecsThanIntegers_throwsTooManySpecs() throws Exception {
      subject = new JavaSpecRunner(FakeContext.withNumSpecs("BigContext", (long)Integer.MAX_VALUE + 1));
      TooManySpecs ex = capture(TooManySpecs.class, subject::testCount);
      assertThat(ex.getMessage(), equalTo("Context BigContext has more specs than JUnit can support: 2147483648"));
    }
  }

  public class old_tests {
    public class getDescription_old {
      private Description description;

      public class givenAContextHierarchy {
        @Before
        public void setup() {
          gateway.init(1,
            FakeContext.nested("Root",
              FakeContext.nested("Middle",
                FakeContext.leaf("Bottom", FakeSpec.with("one"))))
          );
          description = subject.getDescription();
        }

        @Test @Ignore
        public void createsASuiteHierarchyMatchingTheContextHierarchy() throws Exception {
          assertThat(description, isASuiteDescription("Root"));
          assertThat(onlyChild(description), isASuiteDescription("Middle"));
          assertThat(onlyChild(onlyChild(description)), isASuiteDescription("Bottom"));
        }
      }

      public class givenAContext {
        @Before
        public void setup() throws Exception {
          givenTheGatewayHasSpecs(1, FakeContext.leaf("RootId", "RootDisplay", FakeSpec.with("one"), FakeSpec.with("two")));
          description = subject.getDescription();
        }

        @Test @Ignore
        public void createsASuiteDescriptionForThatContext_whereTheSuiteClassNameIsTheContextDisplayName() {
          assertThat(description, isASuiteDescription("RootDisplay"));
        }

        @Test @Ignore
        public void addsATestDescriptionForEachSpecInTheContext() throws Exception {
          assertThat(description.getChildren(), hasSize(2));
        }
      }

      public class givenASpec {
        @Before
        public void setup() throws Exception {
          givenTheGatewayHasSpecs(1, FakeContext.leaf("RootId", "RootDisplay", FakeSpec.with("oneId", "oneDisplay")));
          description = onlyChild(subject.getDescription());
        }

        @Test @Ignore
        public void createsATestDescriptionForThatContext() {
          assertThat(description, isATestDescription());
        }

        @Test @Ignore
        public void usesTheContextDisplayNameForTheTestClassName() {
          assertThat(description.getClassName(), equalTo("RootDisplay"));
        }

        @Test @Ignore
        public void usesTheSpecDisplayNameForTheTestMethodName() {
          assertThat(description.getMethodName(), equalTo("oneDisplay"));
        }
      }

      //Two context classes in different parts of the hierarchy can have the same simple name.
      //Make sure something unique is used for each Suite Description uniqueId.
      public class givenAContextHierarchyWith2OrMoreInstancesOfTheSameDisplayName {
        @Before
        public void setup() {
          FakeContext leftDuplicate = FakeContext.leaf("Root$LeftSuite$SameClassName", "SameClassName", FakeSpec.with("one"));
          FakeContext rightDuplicate = FakeContext.leaf("Root$RightSuite$SameClassName", "SameClassName", FakeSpec.with("one"));
          assumeThat(leftDuplicate.getDisplayName(), equalTo(rightDuplicate.getDisplayName())); //Basis for Description.fUniqueId

          gateway.init(2, FakeContext.nested("Root",
              FakeContext.nested("LeftSuite", leftDuplicate),
              FakeContext.nested("RightSuite", rightDuplicate))
          );
          description = subject.getDescription();
        }

        @Test @Ignore
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
          FakeSpec leftSpec = FakeSpec.with("LeftSuite.same_name", "same name");
          FakeSpec rightSpec = FakeSpec.with("RightSuite.same_name", "same name");
          assumeThat(leftSpec.getDisplayName(), equalTo(rightSpec.getDisplayName())); //Part of Description.fUniqueId

          FakeContext leftDuplicate = FakeContext.leaf("Root$LeftSuite$SameClassName", "SameClassName", leftSpec);
          FakeContext rightDuplicate = FakeContext.leaf("Root$RightSuite$SameClassName", "SameClassName", rightSpec);
          assumeThat(leftDuplicate.getDisplayName(), equalTo(rightDuplicate.getDisplayName())); //Part of Description.fUniqueId

          gateway.init(2, FakeContext.nested("Root",
              FakeContext.nested("LeftSuite", leftDuplicate),
              FakeContext.nested("RightSuite", rightDuplicate))
          );
          description = subject.getDescription();
        }

        @Test @Ignore
        public void theTestDescriptionsAreNotEqual() {
          Description leftFork = onlyChild(description.getChildren().get(0));
          Description one = onlyChild(leftFork);

          Description rightFork = onlyChild(description.getChildren().get(1));
          Description other = onlyChild(rightFork);
          assertThat(one, not(equalTo(other)));
        }
      }
    }

    public class run_old {
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
          givenTheGatewayHasSpecs(1, FakeContext.leaf("Root",
            FakeSpec.with("Root::one", "one"),
            FakeSpec.with("Root::two", "two")
          ));
          subject.run(notifier);
        }

        @Test @Ignore
        public void runsEachSpecInTheContext() {
          assertThat(gateway.rootContext.runCounts(), equalTo(newArrayList(1, 1)));
        }
      }

      public class givenSpecsIn1OrMoreContexts {
        @Before
        public void setup() throws Exception {
          givenTheGatewayHasSpecs(1, FakeContext.nested("Root",
            FakeContext.leaf("Left", FakeSpec.with("Left::one")),
            FakeContext.leaf("Right", FakeSpec.with("Right::one"))
          ));
          subject.run(notifier);
        }

        @Test @Ignore
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
          givenTheGatewayHasSpecs(1, FakeContext.leaf("Root", FakeSpec.with("Root::one", "one")));
          suiteDescription = subject.getDescription();
          subject.run(notifier);
        }

        @Test @Ignore
        public void usesAnEquivalentDescriptionToTheOneReturnedFrom_getDescription() {
          assertThat(suiteDescription.getChildren().get(0), equalTo(events.get(0).description));
        }
      }

      public class givenAnIgnoredSpec {
        @Before
        public void setup() throws Exception {
          givenTheGatewayHasSpecs(1, FakeContext.leaf("Root", FakeSpec.anIgnoredSpec("Root::ignore_me")));
          subject.run(notifier);
        }

        @Test @Ignore
        public void notifiesIgnored() {
          assertThat(onlyElement(events).name, equalTo("testIgnored"));
          assertThat(onlyElement(events).description, equalTo(onlyChild(subject.getDescription())));
        }

        @Test @Ignore
        public void doesNotRunTheSpec() {
          assertThat(gateway.rootContext.runCounts(), equalTo(newArrayList(0)));
        }

        @Test @Ignore
        public void firesNoOtherEvents() {
          assertThat(events, hasSize(1));
        }
      }

      public class whenASpecPasses {
        @Before
        public void setup() throws Exception {
          givenTheGatewayHasSpecs(1, FakeContext.leaf("Root", FakeSpec.with("Root::passes")));
          subject.run(notifier);
        }

        @Test @Ignore
        public void notifiesTestStarted() {
          assertThat(events.get(0).name, equalTo("testStarted"));
          assertThat(events.get(0).description, equalTo(onlyChild(subject.getDescription())));
        }

        @Test @Ignore
        public void runsTheSpec() {
          assertThat(gateway.rootContext.runCounts(), equalTo(newArrayList(1)));
        }

        @Test @Ignore
        public void notifiesTestPassed() {
          assertThat(events.get(1).name, equalTo("testFinished"));
          assertThat(events.get(1).description, equalTo(onlyChild(subject.getDescription())));
        }

        @Test @Ignore
        public void firesNoOtherEvents() {
          assertThat(events, hasSize(2));
        }
      }

      public class whenASpecFails {
        @Before
        public void setup() throws Exception {
          givenTheGatewayHasSpecs(1, FakeContext.leaf("Root",
            FakeSpec.aFailingSpec("Root::one_try_and_fail", new AssertionError("Tricksy AssertionError, like JUnit throws")),
            FakeSpec.aFailingSpec("Root::another_try_and_fail", new RuntimeException("Unchecked exception"))
          ));
          subject.run(notifier);
        }

        @Test @Ignore
        public void notifiesTestStarted() {
          Set<Description> started = run_old.this.events.stream()
            .filter(x -> "testStarted".equals(x.name))
            .map(x -> x.description)
            .collect(Collectors.toSet());

          shouldDescribeAllTests(started);
        }

        @Test @Ignore
        public void notifiesTestFailed_withTheExceptionThatTriggeredFailure() {
          Set<Description> failedDescriptions = run_old.this.events.stream()
            .filter(x -> "testFailure".equals(x.name))
            .map(x -> x.description)
            .collect(Collectors.toSet());
          shouldDescribeAllTests(failedDescriptions);

          Set<Class<?>> failedCauses = run_old.this.events.stream()
            .filter(x -> "testFailure".equals(x.name))
            .map(x -> x.failure.getException())
            .map(x -> x.getClass())
            .collect(Collectors.toSet());
          assertThat(failedCauses, equalTo(newHashSet(RuntimeException.class, AssertionError.class)));
        }

        @Test @Ignore
        public void firesNoOtherEvents() {
          assertThat(events, hasSize(4));
        }

        @Test @Ignore
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

    private void givenTheGatewayHasSpecs(long numSpecs, FakeContext rootContext) {
      gateway.init(numSpecs, rootContext);
    }

    private Matcher<Description> isATestDescription() {
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

    private Matcher<Description> isASuiteDescription(String className) {
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
    public String rootContextId() { return rootContext.getId(); }

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

  //TODO KDK: Remove unused factory methods once behavior has migrated
  private static final class FakeContext extends ClassContext {
    public final List<FakeSpec> specs;
    public final List<FakeContext> subcontexts;
    private long numSpecs;
    private Description description;

    public static Context anyValid() {
      Context context = mock(Context.class);
      when(context.hasSpecs()).thenReturn(true);
      when(context.numSpecs()).thenReturn(1L);
      return context;
    }

    public static FakeContext leaf(String id, FakeSpec... specs) {
      return new FakeContext(id, id, Arrays.asList(specs), new ArrayList<>(0));
    }

    public static FakeContext leaf(String id, String displayName, FakeSpec... specs) {
      return new FakeContext(id, displayName, Arrays.asList(specs), new ArrayList<>(0));
    }

    public static FakeContext nested(String id, FakeContext... subcontexts) {
      return new FakeContext(id, id, new ArrayList<>(0), Arrays.asList(subcontexts));
    }

    public static FakeContext withDescription(Description description) {
      FakeContext context = new FakeContext("Root", "Root", 1);
      context.description = description;
      return context;
    }

    public static FakeContext withNumSpecs(String id, long numSpecs) {
      return new FakeContext(id, id, numSpecs);
    }

    public static FakeContext withSpecs(FakeSpec... specs) {
      return new FakeContext("root", "root", Arrays.asList(specs), new ArrayList<>(0));
    }

    public static FakeContext withNoSpecs(String id, String displayName) {
      return new FakeContext(id, displayName, new ArrayList<>(0), new ArrayList<>(0));
    }

    private FakeContext(String id, String displayName, long numSpecs) {
      super(id, displayName, JavaSpecRunnerTest.class);
      this.specs = new ArrayList<>(0);
      this.subcontexts = new ArrayList<>(0);
      this.numSpecs = numSpecs;
    }

    private FakeContext(String id, String displayName, List<FakeSpec> specs, List<FakeContext> subcontexts) {
      super(id, displayName, JavaSpecRunnerTest.class);
      this.specs = specs;
      this.subcontexts = subcontexts;
      this.numSpecs = this.specs.size();
    }

    public List<Integer> runCounts() {
      return specs.stream().map(x -> x.runCount).collect(toList());
    }

    @Override
    public Description getDescription() { return description; }

    @Override
    public boolean hasSpecs() { return numSpecs > 0; }

    @Override
    public long numSpecs() { return numSpecs; }
  }

  private static class FakeSpec extends Spec {
    private final boolean isIgnored;
    public int runCount;

    public static FakeSpec aFailingSpec(String id, AssertionError toThrow) {
      return new FakeSpec(id, id, false) {
        @Override
        public void run() { throw toThrow; }
      };
    }

    public static FakeSpec aFailingSpec(String id, RuntimeException toThrow) {
      return new FakeSpec(id, id, false) {
        @Override
        public void run() { throw toThrow; }
      };
    }

    public static FakeSpec anIgnoredSpec(String id) {
      return new FakeSpec(id, id, true);
    }

    public static FakeSpec with(String id) {
      return new FakeSpec(id, id, false);
    }

    public static FakeSpec with(String id, String displayName) {
      return new FakeSpec(id, displayName, false);
    }

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