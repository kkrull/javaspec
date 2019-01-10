package info.javaspec.lang.lambda;

import info.javaspec.SpecReporter;
import org.hamcrest.Matchers;

import static org.junit.Assert.assertThat;

public final class MockSpec extends DescriptiveSpec {
  private final String intendedBehavior;
  private boolean runCalled;

  private MockSpec(String intendedBehavior, BehaviorVerification verification) {
    super(intendedBehavior, verification);
    this.intendedBehavior = intendedBehavior;
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
    return String.format("MockSpec{intendedBehavior='%s', runCalled=%s}",
      intendedBehavior,
      runCalled
    );
  }

  public static final class Builder {
    private String intendedBehavior;
    private BehaviorVerification verification;

    public Builder() {
      this.intendedBehavior = "<default intendedBehavior>";
      this.verification = () -> { };
    }

    public Builder withIntendedBehavior(String intendedBehavior) {
      this.intendedBehavior = intendedBehavior;
      return this;
    }

    public Builder thatFailsWith(AssertionError error) {
      this.verification = () -> { throw error; };
      return this;
    }

    public Builder thatPasses() {
      this.verification = () -> { };
      return this;
    }

    public MockSpec build() {
      return new MockSpec(this.intendedBehavior, this.verification);
    }
  }
}
