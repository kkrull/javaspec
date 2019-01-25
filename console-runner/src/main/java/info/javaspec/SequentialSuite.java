package info.javaspec;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static java.util.stream.Collectors.toList;

/** Runs specs in the order they are added */
public final class SequentialSuite implements Suite {
  private final String description;
  private final List<Spec> specs;
  private final List<Suite> children;

  public SequentialSuite() {
    this.description = "<root suite>";
    this.specs = new LinkedList<>();
    this.children = new LinkedList<>();
  }

  public SequentialSuite(String description) {
    this.description = description;
    this.specs = new LinkedList<>();
    this.children = new LinkedList<>();
  }

  public void addChildSuite(Suite suite) {
    this.children.add(suite);
  }

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
