package info.javaspec.console;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.SpecReporter;
import info.javaspec.SpecCollection;
import info.javaspec.lang.lambda.InstanceSpecFinder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.Arrays;

import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@RunWith(HierarchicalContextRunner.class)
public class RunSpecsCommandTest {
  private RunSpecsCommand subject;
  private InstanceSpecFinder specFinder;
  private SpecReporter reporter;
  private SpecCollection collection;

  public class run {
    @Before
    public void setup() throws Exception {
      specFinder = Mockito.mock(InstanceSpecFinder.class);
      collection = Mockito.mock(SpecCollection.class);
      Mockito.when(specFinder.findSpecs(Mockito.any()))
        .thenReturn(collection);

      reporter = Mockito.mock(SpecReporter.class);
      Mockito.when(reporter.hasFailingSpecs()).thenReturn(false);
    }

    @Test
    public void findsSpecsInTheSpecifiedClassesInTheSpecifiedOrder() throws Exception {
      subject = new RunSpecsCommand(specFinder, Arrays.asList(
        "info.javaspec.console.OneSpec",
        "info.javaspec.console.OtherSpec"
      ));

      subject.run(reporter);
      Mockito.verify(specFinder).findSpecs(Arrays.asList(
        OneSpec.class,
        OtherSpec.class
      ));
    }

    @Test
    public void runsTheReturnedCollection() throws Exception {
      subject = new RunSpecsCommand(specFinder, singletonList("info.javaspec.console.OneSpec"));
      subject.run(reporter);
      Mockito.verify(collection).runSpecs(reporter);
    }

    @Test
    public void reportsTheRunStartingAndFinishing() throws Exception {
      subject = new RunSpecsCommand(specFinder, singletonList("info.javaspec.console.OneSpec"));
      subject.run(reporter);
      Mockito.verify(reporter).runStarting();
      Mockito.verify(reporter).runFinished();
    }

    public class whenAllSpecsAreReportedAsPassing {
      @Test
      public void returns0() throws Exception {
        Mockito.when(reporter.hasFailingSpecs()).thenReturn(false);
        subject = new RunSpecsCommand(specFinder, singletonList("info.javaspec.console.OneSpec"));
        int statusCode = subject.run(reporter);
        assertThat(statusCode, equalTo(0));
      }
    }

    public class whenAnySpecsAreReportedAsFailing {
      @Test
      public void returns1() throws Exception {
        Mockito.when(reporter.hasFailingSpecs()).thenReturn(true);
        subject = new RunSpecsCommand(specFinder, singletonList("info.javaspec.console.OneSpec"));
        int statusCode = subject.run(reporter);
        assertThat(statusCode, equalTo(1));
      }
    }

    public class whenAnySpecClassesCanNotBeLoaded {
      @Test
      public void returns2() throws Exception {
        subject = new RunSpecsCommand(specFinder, singletonList("does.not.Exist"));
        int statusCode = subject.run(reporter);
        assertThat(statusCode, equalTo(2));
      }
    }
  }
}
