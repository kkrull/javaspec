package info.javaspec.console;

import info.javaspec.LambdaSpec;
import info.javaspec.Suite;

class SpecDeclaration {
  private static StaticSuite _suite;

  public static void newContext() {
    _suite = new StaticSuite();
  }

  public static void addSpecToCurrentContext(LambdaSpec spec, String description) {
    _suite.addSpec(spec, description);
  }

  public static Suite createSuite() {
    Suite suite = _suite;
    _suite = null;
    return suite;
  }
}
