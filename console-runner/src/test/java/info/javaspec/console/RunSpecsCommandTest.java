package info.javaspec.console;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import info.javaspec.SpecReporter;
import info.javaspec.lang.lambda.InstanceSpecFinder;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.Collections;

@RunWith(HierarchicalContextRunner.class)
public class RunSpecsCommandTest {
  public class run {
    @Before
    public void setup() throws Exception {
    }

    @Test @Ignore //TODO KDK: Work here
    public void loadsTheSpecifiedSpecClasses() throws Exception {
      InstanceSpecFinder specFinder = Mockito.mock(InstanceSpecFinder.class);
      SpecReporter reporter = Mockito.mock(SpecReporter.class);
      Command subject = new RunSpecsCommand(specFinder, Collections.singletonList("info.javaspec.console.OneSpecs"));
      subject.run(reporter);
      Mockito.verify(specFinder).findSpecs(Collections.singletonList(OneSpecs.class));
    }
  }
}
