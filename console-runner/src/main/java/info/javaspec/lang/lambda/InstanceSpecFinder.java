package info.javaspec.lang.lambda;

import info.javaspec.Suite;
import info.javaspec.lang.lambda.Exceptions.SpecDeclarationFailed;

import java.util.List;

/** Creates specs by instantiating a class that declares them during instance initialization */
public class InstanceSpecFinder {
  private final SpecContextFactory contextFactory;

  public InstanceSpecFinder() { //TODO KDK: Take in SpecContextFactory as a parameter
    contextFactory = declarer -> {
      SpecDeclaration.beginDeclaration();
      SpecDeclaration context = SpecDeclaration.getInstance();
      declarer.declareSpecs(context);
      return SpecDeclaration.endDeclaration();
    };
  }

  public InstanceSpecFinder(SpecContextFactory contextFactory) {
    this.contextFactory = contextFactory;
  }

  public Suite findSpecs(List<Class<?>> specClasses) {
    return this.contextFactory.withContext(_context -> {
      for(Class<?> specClass : specClasses) {
        try {
          specClass.newInstance();
        } catch(Exception e) {
          throw SpecDeclarationFailed.whenInstantiating(specClass, e);
        }
      }
    });
  }

  @FunctionalInterface
  interface SpecContextFactory {
    Suite withContext(DeclarerOfSpecs mod);
  }

  @FunctionalInterface
  interface DeclarerOfSpecs {
    void declareSpecs(SpecDeclaration context);
  }
}
