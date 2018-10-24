package info.javaspec.console;

import info.javaspec.LambdaSpec;
import info.javaspec.SpecReporter;
import info.javaspec.Suite;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class Prototype {
  private Prototype() { /* static class */ }

  static final class StaticSuite implements Suite {
    private final List<LambdaSpec> specs;

    public StaticSuite(LambdaSpec... specs) {
      this.specs = Stream.of(specs).collect(Collectors.toList());
    }

    @Override
    public void runSpecs(SpecReporter reporter) {
      for(LambdaSpec spec : specs) {
        reporter.specStarting(spec);
        try {
          spec.run();
        } catch(AssertionError e) {
          reporter.specFailed(spec);
        }

        reporter.specPassed(spec);
      }
    }
  }
}
