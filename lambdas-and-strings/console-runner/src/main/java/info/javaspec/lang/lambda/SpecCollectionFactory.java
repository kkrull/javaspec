package info.javaspec.lang.lambda;

import info.javaspec.SpecCollection;

@FunctionalInterface
public interface SpecCollectionFactory {
  SpecCollection declareSpecs();
}
