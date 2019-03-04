package info.javaspec.lang.lambda;

import info.javaspec.Spec;
import info.javaspec.SpecReporter;
import info.javaspec.Suite;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static java.util.stream.Collectors.toList;

/** Runs specs in the order they are added */
final class SequentialSuite implements WritableSuite {
  private final String description;
  private final List<Spec> specs;
  private final List<Suite> children;

  public SequentialSuite(String description) {
    this.description = description;
    this.specs = new LinkedList<>();
    this.children = new LinkedList<>();
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
    return this.description;
  }

  @Override
  public List<String> intendedBehaviors() {
    return this.specs.stream()
      .map(Spec::intendedBehavior)
      .collect(toList());
  }

  @Override
  public void runSpecs(SpecReporter reporter) {
    reporter.suiteStarting(this);
    this.specs.forEach(x -> x.run(reporter));
    this.children.forEach(x -> x.runSpecs(reporter));
  }

  @Override
  public String toString() {
    return String.format("SequentialSuite{description='%s'}", description);
  }
}
