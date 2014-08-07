package org.jspec.runner;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class FieldExampleTest {
  @Test
  public void work_here() {
    fail("work here");
  }
}