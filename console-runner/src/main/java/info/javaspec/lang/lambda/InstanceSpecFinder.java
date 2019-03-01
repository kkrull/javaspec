package info.javaspec.lang.lambda;

import info.javaspec.Suite;
import info.javaspec.lang.lambda.Exceptions.SpecDeclarationFailed;

import java.util.List;

/** Creates specs by instantiating a class that declares them during instance initialization */
public class InstanceSpecFinder {
  private final DeclarationScopeFactory scopeFactory;

  public InstanceSpecFinder() { //TODO KDK: Take in DeclarationScopeFactory as a parameter
    scopeFactory = strategy -> {
      SpecDeclaration.beginDeclaration();
      strategy.declareSpecs();
      return SpecDeclaration.endDeclaration();
    };
  }

  public InstanceSpecFinder(DeclarationScopeFactory scopeFactory) {
    this.scopeFactory = scopeFactory;
  }

  public Suite findSpecs(List<Class<?>> specClasses) {
    SpecDeclarationStrategy instantiationStrategy = () -> {
      for(Class<?> specClass : specClasses) {
        try {
          specClass.newInstance();
        } catch(Exception e) {
          throw SpecDeclarationFailed.whenInstantiating(specClass, e);
        }
      }
    };

    return this.scopeFactory.declareInOwnScope(instantiationStrategy);
  }

  @FunctionalInterface
  interface DeclarationScopeFactory {
    Suite declareInOwnScope(SpecDeclarationStrategy strategy);
  }

  @FunctionalInterface
  interface SpecDeclarationStrategy {
    void declareSpecs();
  }
}
