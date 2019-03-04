package info.javaspec.lang.lambda;

import info.javaspec.Suite;

import java.util.Optional;

import static info.javaspec.lang.lambda.Exceptions.*;

/**
 * The functional- or Mocha-style syntax for JavaSpec that lets you declare specs with strings and lambdas.
 *
 * Intended use: Static import these methods and call them in a test class's constructor (or instance initializer).
 * Anti-pattern: Calling these methods from a static initializer.
 */
public final class FunctionalDsl { //TODO KDK: Test
  private static DeclarationScope _instance;

  private FunctionalDsl() { /* static class */ }

  public static void openScope() {
    if(_instance != null)
      throw new DeclarationAlreadyStarted();

    _instance = new DeclarationScope();
  }

  public static Suite closeScope() {
    Suite rootCollection = _instance.createRootCollection();
    _instance = null;
    return rootCollection;
  }

  public static void describe(String subject, BehaviorDeclaration describeBehavior) {
    declarationScope().declareSpecsFor(subject, describeBehavior);
  }

  public static void it(String shouldDoWhat, BehaviorVerification verifyBehavior) {
    declarationScope().createSpec(shouldDoWhat, verifyBehavior);
  }

  private static DeclarationScope declarationScope() {
    return Optional.ofNullable(_instance)
      .orElseThrow(DeclarationNotStarted::new);
  }
}
