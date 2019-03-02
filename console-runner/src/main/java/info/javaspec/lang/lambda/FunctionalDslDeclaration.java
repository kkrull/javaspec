package info.javaspec.lang.lambda;

import info.javaspec.Suite;

import java.util.Optional;

import static info.javaspec.lang.lambda.Exceptions.DeclarationAlreadyStarted;
import static info.javaspec.lang.lambda.Exceptions.DeclarationNotStarted;

public class FunctionalDslDeclaration {
  private static DeclarationScope _instance;

  public static void beginDeclaration() {
    if(_instance != null)
      throw new DeclarationAlreadyStarted();

    _instance = new DeclarationScope();
  }

  public static Suite endDeclaration() {
    Suite suite = _instance.completeSuite();
    _instance = null;
    return suite;
  }

  public static DeclarationScope getInstance() {
    return Optional.ofNullable(_instance)
      .orElseThrow(DeclarationNotStarted::new);
  }

  static void reset() {
    _instance = null;
  }
}
