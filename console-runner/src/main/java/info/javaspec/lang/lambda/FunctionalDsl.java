package info.javaspec.lang.lambda;

public final class FunctionalDsl {
  private FunctionalDsl() { /* static class */ }

  public static void it(String shouldDoWhat, SpecRunnable verifyBehavior) {
    SpecDeclaration.createSpecInCurrentContext(verifyBehavior, shouldDoWhat);
  }
}
