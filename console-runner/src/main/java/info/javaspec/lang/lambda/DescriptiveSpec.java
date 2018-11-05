package info.javaspec.lang.lambda;

import info.javaspec.Spec;
import info.javaspec.SpecReporter;

final class DescriptiveSpec implements Spec {
  private final SpecRunnable thunk;
  private final String description;

  public DescriptiveSpec(SpecRunnable thunk, String description) {
    this.thunk = thunk;
    this.description = description;
  }

  @Override
  public void run(SpecReporter reporter) {
    reporter.specStarting(this, this.description);
    try {
      this.thunk.run();
    } catch(AssertionError e) {
      reporter.specFailed(this);
      return;
    }

    reporter.specPassed(this);
  }
}
