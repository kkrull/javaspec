package info.javaspec.lang.lambda;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.RunObserver;
import info.javaspec.SpecCollection;
import info.javaspec.console.Result;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@RunWith(HierarchicalContextRunner.class)
public class RunSpecsCommandTest {
  private RunSpecsCommand subject;
  private SpecCollectionFactory factory;
  private RunObserver observer;
  private SpecCollection collection;

  public class run {
    @Before
    public void setup() throws Exception {
      factory = Mockito.mock(SpecCollectionFactory.class);
      collection = Mockito.mock(SpecCollection.class);
      Mockito.when(factory.declareSpecs()).thenReturn(collection);

      observer = Mockito.mock(RunObserver.class);
      Mockito.when(observer.hasFailingSpecs()).thenReturn(false);
    }

    @Test
    public void declaresSpecs() throws Exception {
      subject = new RunSpecsCommand(factory, observer);
      subject.run();
      Mockito.verify(factory).declareSpecs();
    }

    @Test
    public void runsTheReturnedCollection() throws Exception {
      subject = new RunSpecsCommand(factory, observer);
      subject.run();
      Mockito.verify(collection).runSpecs(observer);
    }

    @Test
    public void returns0WhenThereAreNoFailingSpecs() throws Exception {
      subject = new RunSpecsCommand(factory, observer);
      Result result = subject.run();
      assertThat(result.exitCode, equalTo(0));
    }

    @Test
    public void returns1AndASummaryWhenAnySpecsFail() throws Exception {
      Mockito.when(observer.hasFailingSpecs()).thenReturn(true);

      subject = new RunSpecsCommand(factory, observer);
      Result result = subject.run();
      assertThat(result.exitCode, equalTo(1));
      assertThat(result.summary(), equalTo("Specs failed"));
    }

    @Test
    public void returns2AndTheExceptionWhenSpecDeclarationThrows() throws Exception {
      RuntimeException exception = new RuntimeException("bang!");
      Mockito.when(factory.declareSpecs())
        .thenThrow(exception);

      subject = new RunSpecsCommand(factory, observer);
      Result result = subject.run();
      assertThat(result.exitCode, equalTo(2));
      assertThat(result.exception, equalTo(exception));
    }
  }
}
