package info.javaspec.console;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.SpecCollection;
import info.javaspec.RunObserver;
import info.javaspec.lang.lambda.SpecCollectionFactory;
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
      subject = new RunSpecsCommand(factory);
      subject.run(observer);
      Mockito.verify(factory).declareSpecs();
    }

    @Test
    public void runsTheReturnedCollection() throws Exception {
      subject = new RunSpecsCommand(factory);
      subject.run(observer);
      Mockito.verify(collection).runSpecs(observer);
    }

    @Test
    public void reportsTheRunStartingAndFinishing() throws Exception {
      subject = new RunSpecsCommand(factory);
      subject.run(observer);
      Mockito.verify(observer).runStarting();
      Mockito.verify(observer).runFinished();
    }

    @Test
    public void returns0WhenThereAreNoFailingSpecs() throws Exception {
      subject = new RunSpecsCommand(factory);
      int statusCode = subject.run(observer);
      assertThat(statusCode, equalTo(0));
    }

    @Test
    public void returns1WhenAnySpecsFail() throws Exception {
      Mockito.when(observer.hasFailingSpecs()).thenReturn(true);

      subject = new RunSpecsCommand(factory);
      int statusCode = subject.run(observer);
      assertThat(statusCode, equalTo(1));
    }

    @Test
    public void returns2WhenSpecDeclarationThrows() throws Exception {
      Mockito.when(factory.declareSpecs())
        .thenThrow(new RuntimeException("bang!"));

      subject = new RunSpecsCommand(factory);
      int statusCode = subject.run(observer);
      assertThat(statusCode, equalTo(2));
    }
  }
}
