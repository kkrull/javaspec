package info.javaspec.lang.lambda;

import info.javaspec.RunObserver;
import info.javaspec.Spec;

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
  public void run(RunObserver observer) {
    observer.specStarting(this);
    try {
      this.verification.run();
    } catch(AssertionError e) {
      observer.specFailed(this, e);
      return;
    } catch(Exception e) {
      observer.specFailed(this, e);
      return;
    }

    observer.specPassed(this);
  }
}
