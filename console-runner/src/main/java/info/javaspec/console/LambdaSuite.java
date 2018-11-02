package info.javaspec.console;

import info.javaspec.LambdaSpec;
import info.javaspec.SpecReporter;
import info.javaspec.Suite;

import java.util.LinkedHashMap;
import java.util.Map;

final class LambdaSuite implements Suite {
  private final Map<LambdaSpec, String> specs;

  public LambdaSuite() {
    this.specs = new LinkedHashMap<>();
  }

  public void addSpec(LambdaSpec spec, String description) {
    this.specs.put(spec, description); //TODO KDK: Try having description as a property of Spec
  }

  @Override
  public void runSpecs(SpecReporter reporter) {
    for(Map.Entry<LambdaSpec, String> entry : specs.entrySet()) {
      LambdaSpec spec = entry.getKey();
      String description = entry.getValue();
      reporter.specStarting(spec, description);
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
