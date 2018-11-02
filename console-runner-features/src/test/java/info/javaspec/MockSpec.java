package info.javaspec;

import org.hamcrest.Matchers;

import static org.junit.Assert.assertThat;

public final class MockSpec implements Spec {
  private boolean runCalled;
  private AssertionError runThrows;

  public static MockSpec runPasses() {
    return new MockSpec(null);
  }

  public static MockSpec runThrows(AssertionError e) {
    return new MockSpec(e);
  }

  private MockSpec(AssertionError runThrows) {
    this.runCalled = false;
    this.runThrows = runThrows;
  }

  @Override
  public void run() {
    this.runCalled = true;

    if(this.runThrows != null)
      throw this.runThrows;
  }

  public void runShouldHaveBeenCalled() {
    assertThat(this.runCalled, Matchers.equalTo(true));
  }

  @Override
  public String toString() {
    return String.format("MockSpec{runCalled=%s, runThrows=%s}", runCalled, runThrows);
  }
}
