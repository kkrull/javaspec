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
  private Runner subject;

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

  //TODO KDK: Remove unused factory methods once behavior has migrated
  public static final class FakeContext extends ClassContext {
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