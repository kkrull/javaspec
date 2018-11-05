package info.javaspec.lang.lambda;

import info.javaspec.Spec;
import info.javaspec.SequentialSuite;
import info.javaspec.SpecReporter;
import info.javaspec.Suite;

public final class SpecDeclaration {
  private static SequentialSuite _suite;

  public static void newContext() {
    _suite = new SequentialSuite();
  }

  public static void addSpecToCurrentContext(SpecRunnable thunk, String description) {
    Spec spec = new DescribedSpec(thunk, description);
    _suite.addSpec(spec);
  }

  public static Suite createSuite() {
    Suite suite = _suite;
    _suite = null;
    return suite;
  }

  private static final class DescribedSpec implements Spec {
    private final SpecRunnable thunk;
    private final String description;

    public DescribedSpec(SpecRunnable thunk, String description) {
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
}
