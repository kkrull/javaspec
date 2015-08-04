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

import static info.javaspec.testutil.Assertions.capture;
import static info.javaspec.testutil.Matchers.matchesRegex;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

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
      Context rootContext = MockContext.withSpecs();
      subject = new JavaSpecRunner(rootContext);
      subject.run(mock(RunNotifier.class));
      verify(rootContext).run(Mockito.any());
    }
  }

  public class testCount {
    @Test
    public void givenAContextWithAnIntegerNumberOfSpecs_delegatesToTheRootContext() throws Exception {
      subject = new JavaSpecRunner(FakeContext.withNumSpecs(2));
      assertThat(subject.testCount(), equalTo(2));
    }

    @Test
    public void givenAContextWithMoreSpecsThanIntegers_throwsTooManySpecs() throws Exception {
      subject = new JavaSpecRunner(FakeContext.withNumSpecs("BigContext", (long)Integer.MAX_VALUE + 1));
      TooManySpecs ex = capture(TooManySpecs.class, subject::testCount);
      assertThat(ex.getMessage(), equalTo("Context BigContext has more specs than JUnit can support: 2147483648"));
    }
  }
}
