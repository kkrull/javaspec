package info.javaspec.lang.lambda;

import info.javaspec.Suite;
import info.javaspec.lang.lambda.Exceptions.SpecDeclarationFailed;

import java.util.List;

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

  public Suite findSpecs(List<Class<?>> specClasses) {
    SpecDeclaration.beginDeclaration();

    for(Class<?> specClass : specClasses) {
      try {
        specClass.newInstance();
      } catch(Exception e) {
        throw SpecDeclarationFailed.whenInstantiating(specClass, e);
      }
    }

    return SpecDeclaration.endDeclaration();
  }
}
