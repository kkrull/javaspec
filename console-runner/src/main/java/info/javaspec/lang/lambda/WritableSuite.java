package info.javaspec.lang.lambda;

import info.javaspec.Spec;
import info.javaspec.Suite;

public interface WritableSuite extends Suite { //TODO KDK: Rename to WritableSpecCollection
  void addSpec(Spec spec);

  void addSubCollection(Suite collection);
}
