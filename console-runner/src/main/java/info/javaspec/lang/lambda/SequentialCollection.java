package info.javaspec.lang.lambda;

import info.javaspec.RunObserver;
import info.javaspec.Spec;
import info.javaspec.SpecCollection;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static java.util.stream.Collectors.toList;

/** Describes a subject with any number of specs and sub-collections, both of which run in the order they are added. */
final class SequentialCollection implements SubjectCollection, CompositeSpecCollection {
  private final String description;
  private final List<Spec> specs;
  private final List<SpecCollection> children;

  public SequentialCollection(String description) {
    this.description = description;
    this.specs = new LinkedList<>();
    this.children = new LinkedList<>();
  }

  @Override
  public void addSpec(Spec spec) {
    this.specs.add(spec);
  }

  @Override
  public void addSubCollection(SpecCollection collection) {
    this.children.add(collection);
  }

  @Override
  public String description() {
    return this.description;
  }

  @Override
  public List<String> intendedBehaviors() {
    return this.specs.stream()
      .map(Spec::intendedBehavior)
      .collect(toList());
  }

  @Override
  public void runSpecs(RunObserver observer) {
    observer.collectionStarting(this);
    this.specs.forEach(x -> x.run(observer));
    this.children.forEach(x -> x.runSpecs(observer));
  }

  @Override
  public List<SpecCollection> subCollections() {
    return new ArrayList<>(this.children);
  }

  @Override
  public String toString() {
    return String.format("SequentialCollection{description='%s'}", description);
  }
}
