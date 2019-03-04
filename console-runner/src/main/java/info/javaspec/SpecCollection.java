package info.javaspec;

import java.util.List;

/** An ordered collection of Specs */
public interface SpecCollection {
  String description();

  List<String> intendedBehaviors();

  void runSpecs(SpecReporter reporter);

  List<SpecCollection> subCollections();
}
