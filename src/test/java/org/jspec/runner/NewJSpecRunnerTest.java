package org.jspec.runner;

import org.junit.Test;
import org.junit.runner.RunWith;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class NewJSpecRunnerTest {
  public class constructor {
    private TestConfiguration makeConfiguration() {
      return new TestConfiguration() {};
    }
    
    @Test
    public void givenAConfiguration() {
      new NewJSpecRunner(makeConfiguration());
    }
  }
}