package info.javaspec.lang.lambda;

import info.javaspec.SpecCollection;

/** Producer view of a collection that may contain more sub-collections for more specific contexts or circumstances. */
interface CompositeSpecCollection extends SpecCollection {
  void addSubCollection(SpecCollection collection);
}
