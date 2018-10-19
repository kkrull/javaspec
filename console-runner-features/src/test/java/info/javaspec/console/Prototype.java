package info.javaspec.console;

import info.javaspec.LambdaSpec;
import info.javaspec.SpecReporter;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class Prototype {
  private Prototype() { /* static class */ }

  static final class SpecRunner {
    private final SpecReporter reporter;

    static void main(Suite suite, SpecReporter reporter, ExitHandler system) {
      SpecRunner runner = new SpecRunner(reporter);
      runner.run(suite);

      int exitCode = reporter.hasFailingSpecs() ? 1 : 0;
      system.exit(exitCode);
    }

    private SpecRunner(SpecReporter reporter) {
      this.reporter = reporter;
    }

    private void run(Suite suite) {
      suite.runSpecs(this.reporter);
    }
  }

  static final class Suite {
    private final List<LambdaSpec> specs;

    public Suite(LambdaSpec... specs) {
      this.specs = Stream.of(specs).collect(Collectors.toList());
    }

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
