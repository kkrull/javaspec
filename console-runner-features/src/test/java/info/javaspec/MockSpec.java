package info.javaspec;

import org.hamcrest.Matchers;

import static org.junit.Assert.assertThat;

public final class MockSpec implements Spec {
  private final String description;
  private final AssertionError reportSpecFailure;
  private final boolean reportSpecPassing;

  private boolean runCalled;

  private MockSpec(String description, AssertionError reportSpecFailure, boolean reportSpecPassing) {
    this.description = description;
    this.reportSpecFailure = reportSpecFailure;
    this.reportSpecPassing = reportSpecPassing;
    this.runCalled = false;
  }

  @Override
  public void run(SpecReporter reporter) {
    this.runCalled = true;
    reporter.specStarting(this, this.description);

    if(this.reportSpecFailure != null)
      reporter.specFailed(this);

    if(this.reportSpecPassing)
      reporter.specPassed(this);
  }

  public void runShouldHaveBeenCalled() {
    assertThat(this.runCalled, Matchers.equalTo(true));
  }

  @Override
  public String toString() {
    return String.format("MockSpec{description='%s', reportSpecFailure=%s, reportSpecPassing=%s, runCalled=%s}",
      description,
      reportSpecFailure,
      reportSpecPassing,
      runCalled
    );
  }

  public static final class Builder {
    private String description;
    private AssertionError reportSpecFailure;
    private boolean reportSpecPassing;

    public Builder() {
      this.reportSpecPassing = false;
    }

    public Builder describedAs(String description) {
      this.description = description;
      return this;
    }

    public Builder reportsSpecFailure(AssertionError error) {
      this.reportSpecFailure = error;
      return this;
    }

    public Builder reportsSpecPassing() {
      this.reportSpecPassing = true;
      return this;
    }

    public MockSpec build() {
      return new MockSpec(this.description, this.reportSpecFailure, this.reportSpecPassing);
    }
  }
}
