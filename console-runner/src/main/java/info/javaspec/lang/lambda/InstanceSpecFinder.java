package info.javaspec.lang.lambda;

import info.javaspec.SpecCollection;
import info.javaspec.lang.lambda.Exceptions.SpecDeclarationFailed;

import java.util.List;

/** Creates specs by instantiating a class that declares them during instance initialization */
public class InstanceSpecFinder {
  private final DeclarationScopeFactory scopeFactory;

  public InstanceSpecFinder(DeclarationScopeFactory scopeFactory) {
    this.scopeFactory = scopeFactory;
  }

  public SpecCollection findSpecs(List<Class<?>> specClasses) {
    DeclarationStrategy instantiationStrategy = () -> {
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
  public interface DeclarationScopeFactory {
    SpecCollection declareInOwnScope(DeclarationStrategy strategy);
  }

  @FunctionalInterface
  public interface DeclarationStrategy {
    void declareSpecs();
  }
}
