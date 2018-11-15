package info.javaspec.lang.lambda;

import info.javaspec.SpecReporter;
import org.hamcrest.Matchers;

import static org.junit.Assert.assertThat;

public final class MockSpec extends DescriptiveSpec {
  private final String description;
  private boolean runCalled;

  private MockSpec(String description, SpecRunnable thunk) {
    super(description, thunk);
    this.description = description;
    this.runCalled = false;
  }

  @Override
  public void run(SpecReporter reporter) {
    this.runCalled = true;
    super.run(reporter);
  }

  public void runShouldHaveBeenCalled() {
    assertThat(this.runCalled, Matchers.equalTo(true));
  }

  @Override
  public String toString() {
    return String.format("MockSpec{description='%s', runCalled=%s}",
      description,
      runCalled
    );
  }

  public static final class Builder {
    private String description;
    private SpecRunnable thunk;

    public Builder() {
      this.description = "<default description>";
      this.thunk = () -> { };
    }

    public Builder describedAs(String description) {
      this.description = description;
      return this;
    }

    public Builder thatFailsWith(AssertionError error) {
      this.thunk = () -> { throw error; };
      return this;
    }

    public Builder thatPasses() {
      this.thunk = () -> { };
      return this;
    }

    public MockSpec build() {
      return new MockSpec(this.description, this.thunk);
    }
  }
}
