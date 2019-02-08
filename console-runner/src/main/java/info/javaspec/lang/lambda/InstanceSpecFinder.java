package info.javaspec.lang.lambda;

import info.javaspec.Suite;
import info.javaspec.lang.lambda.Exceptions.SpecDeclarationFailed;

/** Creates specs by instantiating a class that declares them during instance initialization */
public final class InstanceSpecFinder {
  public Suite findSpecs(Class<?> specClass) {
    SpecDeclaration.beginDeclaration();
    try {
      specClass.newInstance();
    } catch(Exception e) {
      throw SpecDeclarationFailed.whenInstantiating(specClass, e);
    }

    return SpecDeclaration.endDeclaration();
  }
}
