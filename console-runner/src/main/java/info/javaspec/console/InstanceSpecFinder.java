package info.javaspec.console;

import info.javaspec.Suite;
import info.javaspec.lang.lambda.SpecDeclaration;

final class InstanceSpecFinder {
  public Suite findSpecs(Class<?> specClass) {
    SpecDeclaration.newContext();
    try {
      specClass.newInstance();
    } catch(Exception e) {
      throw SpecDeclarationFailed.whenInstantiating(specClass, e);
    }

    return SpecDeclaration.createSuite();
  }

  static final class SpecDeclarationFailed extends RuntimeException {
    public static SpecDeclarationFailed whenInstantiating(Class<?> specClass, Exception cause) {
      return new SpecDeclarationFailed(
        String.format("Failed to instantiate spec %s, to declare specs", specClass.getName()),
        cause);
    }

    private SpecDeclarationFailed(String message, Exception cause) {
      super(message, cause);
    }
  }
}
