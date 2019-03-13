package info.javaspec.lang.lambda;

import info.javaspec.SpecCollection;
import info.javaspec.lang.lambda.Exceptions.SpecDeclarationFailed;

import java.util.List;

public class FunctionalDslStrategy implements SpecCollectionFactory {
  private final List<String> classNames;

  public FunctionalDslStrategy(List<String> classNames) {
    this.classNames = classNames;
  }

  @Override
  public SpecCollection declareSpecs() {
    FunctionalDsl.openScope();
    for(String className : this.classNames) {
      Class<?> specClass;
      try {
        specClass = Class.forName(className);
      } catch(ClassNotFoundException | ExceptionInInitializerError e) {
        throw SpecDeclarationFailed.whenLoading(className, e);
      }

      try {
        specClass.newInstance();
      } catch(Exception e) {
        throw SpecDeclarationFailed.whenInstantiating(specClass, e);
      }
    }

    return FunctionalDsl.closeScope();
  }
}
