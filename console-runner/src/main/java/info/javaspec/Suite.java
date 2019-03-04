package info.javaspec;

import java.util.List;

/** An ordered collection of Specs */
public interface Suite { //TODO KDK: Rename to SpecCollection
  String description();

  List<String> intendedBehaviors();

  void runSpecs(SpecReporter reporter);

  List<Suite> subCollections();
}
