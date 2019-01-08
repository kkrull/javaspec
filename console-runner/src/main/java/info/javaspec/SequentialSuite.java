package info.javaspec;

import java.util.LinkedList;
import java.util.List;

import static java.util.stream.Collectors.toList;

/** Runs specs in the order they are added */
public final class SequentialSuite implements Suite {
  private final List<Spec> specs;

  public SequentialSuite() {
    this.specs = new LinkedList<>();
  }

  public void addSpec(Spec spec) {
    this.specs.add(spec);
  }

  @Override
  public void runSpecs(SpecReporter reporter) {
    this.specs.forEach(x -> x.run(reporter));
  }

  @Override
  public List<String> specDescriptions() {
    return this.specs.stream()
      .map(x -> x.description())
      .collect(toList());
  }
}
