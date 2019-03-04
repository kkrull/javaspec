package info.javaspec.lang.lambda;

import info.javaspec.Spec;
import info.javaspec.SpecCollection;

public interface WritableSuite extends SpecCollection {
  void addSpec(Spec spec);

  void addSubCollection(SpecCollection collection);
}
