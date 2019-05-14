package info.javaspec.lang.lambda;

import info.javaspec.Spec;
import info.javaspec.SpecCollection;

/** Producer view of a collection that describes a subject with specs. */
interface SubjectCollection extends SpecCollection {
  void addSpec(Spec spec);
}
