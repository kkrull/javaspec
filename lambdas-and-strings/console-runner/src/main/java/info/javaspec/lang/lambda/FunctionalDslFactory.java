package info.javaspec.lang.lambda;

import info.javaspec.SpecCollection;
import info.javaspec.lang.lambda.Exceptions.SpecDeclarationFailed;

import java.util.List;

public class FunctionalDslFactory implements SpecCollectionFactory {
  private final ClassLoader loader;
  private final List<String> classNames;

  public FunctionalDslFactory(List<String> classNames) {
    this.classNames = classNames;
    this.loader = FunctionalDslFactory.class.getClassLoader();
  }

  public FunctionalDslFactory(ClassLoader loader, List<String> classNames) {
    this.loader = loader;
    this.classNames = classNames;
  }

  @Override
  public SpecCollection declareSpecs() {
    FunctionalDsl.openScope();
    for(String className : this.classNames) {
      Class<?> specClass;
      try {
        specClass = Class.forName(className, true, this.loader);
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
