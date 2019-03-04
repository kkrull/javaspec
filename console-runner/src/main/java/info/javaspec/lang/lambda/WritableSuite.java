package info.javaspec.lang.lambda;

import info.javaspec.Spec;
import info.javaspec.Suite;

public interface WritableSuite extends Suite {
  void addChildSuite(Suite suite);

  void addSpec(Spec spec);
}
