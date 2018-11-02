package info.javaspec.lang.lambda;

import info.javaspec.Spec;
import info.javaspec.SequentialSuite;
import info.javaspec.Suite;

public final class SpecDeclaration {
  private static SequentialSuite _suite;

  public static void newContext() {
    _suite = new SequentialSuite();
  }

  public static void addSpecToCurrentContext(SpecRunnable runnable, String description) {
    Spec spec = runnable::run;
    _suite.addSpec(spec, description);
  }

  public static Suite createSuite() {
    Suite suite = _suite;
    _suite = null;
    return suite;
  }
}
