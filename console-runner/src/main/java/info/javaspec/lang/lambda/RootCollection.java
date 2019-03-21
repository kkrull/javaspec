package info.javaspec.lang.lambda;

import info.javaspec.RunObserver;
import info.javaspec.Spec;
import info.javaspec.SpecCollection;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

final class RootCollection implements WritableSpecCollection {
  private final List<SpecCollection> children;
  private final List<Spec> specs;

  public RootCollection() {
    this.children = new LinkedList<>();
    this.specs = new LinkedList<>();
  }

  @Override
  public void addSubCollection(SpecCollection collection) {
    this.children.add(collection);
  }

  @Override
  public void addSpec(Spec spec) {
    this.specs.add(spec);
  }

  @Override
  public List<SpecCollection> subCollections() {
    return new ArrayList<>(this.children);
  }

  @Override
  public String description() {
    return "";
  }

  @Override
  public List<String> intendedBehaviors() {
    return this.specs.stream()
      .map(Spec::intendedBehavior)
      .collect(Collectors.toList());
  }

  @Override
  public void runSpecs(RunObserver observer) {
    observer.collectionStarting(this);
    this.specs.forEach(x -> x.run(observer));
    this.subCollections().forEach(x -> x.runSpecs(observer));
  }
}
