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
  public String description() {
//    return "Illudium Q-36 Explosive Space Modulator";
    throw new UnsupportedOperationException("work here");
  }

  @Override
  public List<String> intendedBehaviors() {
    return this.specs.stream()
      .map(Spec::intendedBehavior)
      .collect(toList());
  }

  @Override
  public void runSpecs(SpecReporter reporter) {
    this.specs.forEach(x -> x.run(reporter));
  }
}
