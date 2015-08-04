package info.javaspec.runner;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.runner.JavaSpecRunner.NoSpecs;
import info.javaspec.runner.JavaSpecRunner.TooManySpecs;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static info.javaspec.testutil.Assertions.capture;
import static info.javaspec.testutil.Matchers.matchesRegex;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(HierarchicalContextRunner.class)
public class JavaSpecRunnerTest {
  private Runner subject;

  public class constructor {
    @Test
    public void givenAClassWithoutAnySpecs_throwsNoSpecs() throws Exception {
      Context rootContext = FakeContext.withNoSpecs("ContextClasses$Empty");
      Exception ex = capture(NoSpecs.class, () -> new JavaSpecRunner(rootContext));
      assertThat(ex.getMessage(), matchesRegex("^Context ContextClasses[$]Empty must contain at least 1 spec"));
    }
  }

  public class getDescription {
    @Test
    public void delegatesToTheRootContext() throws Exception {
      Description description = Description.createSuiteDescription("Root", 1L);
      subject = new JavaSpecRunner(FakeContext.withDescription(description));
      assertThat(subject.getDescription(), sameInstance(description));
    }
  }

  public class run {
    @Test
    public void delegatesToTheRootContext() {
      Context rootContext = FakeContext.mock();
      subject = new JavaSpecRunner(rootContext);
      subject.run(Mockito.mock(RunNotifier.class));
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

  private static final class FakeContext extends Context {
    public final List<FakeSpec> specs;
    public final List<FakeContext> subcontexts;
    private long numSpecs;
    private Description description;

    public static Context mock() {
      Context context = Mockito.mock(Context.class);
      when(context.hasSpecs()).thenReturn(true);
      when(context.numSpecs()).thenReturn(1L);
      return context;
    }

    public static FakeContext withDescription(Description description) {
      FakeContext context = new FakeContext("Root", 1);
      context.description = description;
      return context;
    }

    public static FakeContext withNumSpecs(String id, long numSpecs) {
      return new FakeContext(id, numSpecs);
    }

    public static FakeContext withSpecs(FakeSpec... specs) {
      return new FakeContext("root", Arrays.asList(specs), new ArrayList<>(0));
    }

    public static FakeContext withNoSpecs(String id) {
      return new FakeContext(id, new ArrayList<>(0), new ArrayList<>(0));
    }

    private FakeContext(String id, long numSpecs) {
      super(id);
      this.specs = new ArrayList<>(0);
      this.subcontexts = new ArrayList<>(0);
      this.numSpecs = numSpecs;
    }

    private FakeContext(String id, List<FakeSpec> specs, List<FakeContext> subcontexts) {
      super(id);
      this.specs = specs;
      this.subcontexts = subcontexts;
      this.numSpecs = this.specs.size();
    }

    @Override
    public Description getDescription() { return description; }

    @Override
    public boolean hasSpecs() { return numSpecs > 0; }

    @Override
    public long numSpecs() { return numSpecs; }

    @Override
    public void run(RunNotifier notifier) { throw new UnsupportedOperationException(); }
  }

  private static class FakeSpec extends Spec {
    public static FakeSpec with(String id) {
      return new FakeSpec(id);
    }

    private FakeSpec(String id) {
      super(id);
    }

    @Override
    public boolean isIgnored() { return false; }

    @Override
    public void run(RunNotifier notifier) { }

    @Override
    public void run() { }
  }
}