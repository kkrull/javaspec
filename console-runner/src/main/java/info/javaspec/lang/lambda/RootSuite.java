package info.javaspec.lang.lambda;

import info.javaspec.Spec;
import info.javaspec.SpecReporter;
import info.javaspec.Suite;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

final class RootSuite implements WritableSuite {
  private final List<Suite> children;
  private final List<Spec> specs;

  public RootSuite() {
    this.children = new LinkedList<>();
    this.specs = new LinkedList<>();
  }

  @Override
  public void addSubCollection(Suite collection) {
    this.children.add(collection);
  }

  @Override
  public void addSpec(Spec spec) {
    this.specs.add(spec);
  }

  @Override
  public List<Suite> subCollections() {
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
  public void runSpecs(SpecReporter reporter) {
    reporter.collectionStarting(this);
    this.specs.forEach(x -> x.run(reporter));
    this.subCollections().forEach(x -> x.runSpecs(reporter));
  }
}
