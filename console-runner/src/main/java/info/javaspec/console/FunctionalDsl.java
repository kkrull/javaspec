package info.javaspec.console;

import info.javaspec.LambdaSpec;

public class FunctionalDsl {
  private FunctionalDsl() { /* static class */ }

  public static void it(String doesWhat, LambdaSpec spec) {
    SpecDeclaration.addSpecToCurrentContext(spec, doesWhat);
  }
}
