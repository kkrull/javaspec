package info.javaspec.lang.lambda;

import info.javaspec.Spec;
import info.javaspec.SpecReporter;

final class DescriptiveSpec implements Spec {
  private final String intendedBehavior;
  private final BehaviorVerification verification;

  public DescriptiveSpec(String intendedBehavior, BehaviorVerification verification) {
    this.intendedBehavior = intendedBehavior;
    this.verification = verification;
  }

  @Override
  public String intendedBehavior() {
    return this.intendedBehavior;
  }

  @Override
  public void run(SpecReporter reporter) {
    reporter.specStarting(this);
    try {
      this.verification.run();
    } catch(AssertionError | Exception e) {
      reporter.specFailed(this);
      return;
    }

    reporter.specPassed(this);
  }
}
