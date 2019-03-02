package info.javaspec.lang.lambda;

/**
 * The functional- or Mocha-style syntax for JavaSpec that lets you declare specs with strings and lambdas.
 *
 * Intended use: Static import these methods and call them in a test class's constructor (or instance initializer).
 * Anti-pattern: Calling these methods from a static initializer.
 */
public final class FunctionalDsl {
  private FunctionalDsl() { /* static class */ }

  public static void describe(String subject, BehaviorDeclaration describeBehavior) {
    FunctionalDslDeclaration.getInstance().declareSpecsFor(subject, describeBehavior);
  }

  public static void it(String shouldDoWhat, BehaviorVerification verifyBehavior) {
    FunctionalDslDeclaration.getInstance().createSpec(shouldDoWhat, verifyBehavior);
  }
}
