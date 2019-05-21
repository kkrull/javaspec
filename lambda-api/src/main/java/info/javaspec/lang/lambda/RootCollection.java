package info.javaspec.lang.lambda;

import info.javaspec.RunObserver;
import info.javaspec.SpecCollection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/** The root of a composite SpecCollection.  It doesn't describe anything, so it has no specs; only children. */
final class RootCollection implements CompositeSpecCollection {
  private final List<SpecCollection> children;

  public RootCollection() {
    this.children = new LinkedList<>();
  }

  @Override
  public void addSubCollection(SpecCollection collection) {
    this.children.add(collection);
  }

  @Override
  public String description() {
    return "";
  }

  @Override
  public List<String> intendedBehaviors() {
    return Collections.emptyList();
  }

  @Override
  public void runSpecs(RunObserver observer) {
    observer.runStarting();
    this.subCollections().forEach(x -> x.runSpecs(observer));
    observer.runFinished();
  }

  @Override
  public List<SpecCollection> subCollections() {
    return new ArrayList<>(this.children);
  }
}
