package info.javaspec;

import org.hamcrest.Matchers;

import static org.junit.Assert.assertThat;

public final class MockSpec implements Spec {
  private final String specStartingDescription;

  private boolean runCalled;
  private AssertionError runThrows;

  public static MockSpec runPasses(String description) {
    return new MockSpec(description, null);
  }

  public static MockSpec runThrows(String description, AssertionError e) {
    return new MockSpec(description, e);
  }

  private MockSpec(String specStartingDescription, AssertionError runThrows) {
    this.specStartingDescription = specStartingDescription;
    this.runCalled = false;
    this.runThrows = runThrows;
  }

  @Override
  public void run(SpecReporter reporter) {
    this.runCalled = true;
    reporter.specStarting(this, this.specStartingDescription);
    if(this.runThrows != null)
      reporter.specFailed(this);
    else
      reporter.specPassed(this);
  }

  public void runShouldHaveBeenCalled() {
    assertThat(this.runCalled, Matchers.equalTo(true));
  }

  @Override
  public String toString() {
    return String.format("MockSpec{runCalled=%s, runThrows=%s}", runCalled, runThrows);
  }
}
