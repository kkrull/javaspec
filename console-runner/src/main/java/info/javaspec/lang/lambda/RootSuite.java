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
  public void addChildSuite(Suite suite) {
    this.children.add(suite);
  }

  @Override
  public void addSpec(Spec spec) {
    this.specs.add(spec);
  }

  @Override
  public List<Suite> childSuites() {
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
    reporter.suiteStarting(this);
    this.specs.forEach(x -> x.run(reporter));
    this.childSuites().forEach(x -> x.runSpecs(reporter));
  }
}
