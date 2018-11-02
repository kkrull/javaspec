package info.javaspec.lang.lambda;

public final class FunctionalDsl {
  private FunctionalDsl() { /* static class */ }

  public static void it(String doesWhat, SpecRunnable findOut) {
    SpecDeclaration.addSpecToCurrentContext(findOut, doesWhat);
  }
}
