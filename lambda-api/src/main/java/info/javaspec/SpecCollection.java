package info.javaspec;

import java.util.List;

/** A collection of related specs, that is a composite with potential sub-collections. */
public interface SpecCollection {
  String description();

  List<String> intendedBehaviors();

  void runSpecs(RunObserver observer);

  List<SpecCollection> subCollections();
}
