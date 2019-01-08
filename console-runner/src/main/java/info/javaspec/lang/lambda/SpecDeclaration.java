package info.javaspec.lang.lambda;

import info.javaspec.Spec;
import info.javaspec.SequentialSuite;
import info.javaspec.Suite;

/** Groups recently-declared specs into a suite of specs that can be run together */
final class SpecDeclaration {
  private static SequentialSuite _suite;

  public static void newContext() {
    _suite = new SequentialSuite();
  }

  public static void createSpecInCurrentContext(SpecRunnable thunk, String description) {
    Spec spec = new DescriptiveSpec(description, thunk);
    _suite.addSpec(spec);
  }

  public static Suite createSuite() {
    Suite suite = _suite;
    _suite = null;
    return suite;
  }
}
