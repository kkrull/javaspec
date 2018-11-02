package info.javaspec;

import java.util.LinkedHashMap;
import java.util.Map;

/** Runs specs in the order they are added */
public final class SequentialSuite implements Suite {
  private final Map<Spec, String> specs;

  public SequentialSuite() {
    this.specs = new LinkedHashMap<>();
  }

  public void addSpec(Spec spec, String description) {
    this.specs.put(spec, description);
  }

  @Override
  public void runSpecs(SpecReporter reporter) {
    for(Map.Entry<Spec, String> entry : specs.entrySet()) {
      Spec spec = entry.getKey();
      String description = entry.getValue();
      reporter.specStarting(spec, description); //TODO KDK: Move the reporting and wrapping to Spec#run(SpecReporter)
      try {
        spec.run();
      } catch(AssertionError e) {
        reporter.specFailed(spec);
        return;
      }

      reporter.specPassed(spec);
    }
  }
}
