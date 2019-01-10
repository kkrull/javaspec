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

  public static void createSpec(String intendedBehavior, BehaviorVerification verification) {
    Spec spec = new DescriptiveSpec(intendedBehavior, verification);
    _suite.addSpec(spec);
  }

  public static Suite createSuite() {
    Suite suite = _suite;
    _suite = null;
    return suite;
  }
}
