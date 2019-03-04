package info.javaspec.lang.lambda;

import info.javaspec.Spec;
import info.javaspec.SpecCollection;

public interface WritableSpecCollection extends SpecCollection {
  void addSpec(Spec spec);

  void addSubCollection(SpecCollection collection);
}
