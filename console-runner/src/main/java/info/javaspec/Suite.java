package info.javaspec;

import java.util.List;

/** An ordered collection of Specs */
public interface Suite {
  String description();

  List<String> intendedBehaviors();

  void runSpecs(SpecReporter reporter);
}
