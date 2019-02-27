package info.javaspec.console;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.SpecReporter;
import info.javaspec.Suite;
import info.javaspec.lang.lambda.InstanceSpecFinder;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.Collections;

@RunWith(HierarchicalContextRunner.class)
public class RunSpecsCommandTest {
  private RunSpecsCommand subject;
  private InstanceSpecFinder specFinder;
  private SpecReporter reporter;

  public class run {
    @Before
    public void setup() throws Exception {
      reporter = Mockito.mock(SpecReporter.class);
      specFinder = Mockito.mock(InstanceSpecFinder.class);
      Mockito.when(specFinder.findSpecs(Mockito.any()))
        .thenReturn(Mockito.mock(Suite.class));
    }

    @Test
    public void loadsTheSpecifiedSpecClasses() throws Exception {
      subject = new RunSpecsCommand(specFinder, Collections.singletonList("info.javaspec.console.OneSpec"));
      subject.run(reporter);
      Mockito.verify(specFinder).findSpecs(Collections.singletonList(OneSpec.class));
    }

    @Test @Ignore
    public void doesTheNextThing() throws Exception {
    }

  }
}
