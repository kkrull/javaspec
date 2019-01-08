package info.javaspec.lang.lambda;

import info.javaspec.Spec;
import info.javaspec.SpecReporter;

class DescriptiveSpec implements Spec {
  private final String description;
  private final SpecRunnable thunk;

  public DescriptiveSpec(String description, SpecRunnable thunk) {
    this.thunk = thunk;
    this.description = description;
  }

  @Override
  public String description() {
    return this.description;
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
