package info.javaspec;

import java.util.List;

/** A composite collection of related specs */
public interface SpecCollection {
  String description();

  List<String> intendedBehaviors();

  void runSpecs(SpecReporter reporter);

  List<SpecCollection> subCollections();
}
