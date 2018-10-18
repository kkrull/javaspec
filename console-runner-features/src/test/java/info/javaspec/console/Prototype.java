package info.javaspec.console;

import info.javaspec.console.Mock.MockSpec;
import info.javaspec.console.Mock.MockSpecReporter;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class Prototype {
  private Prototype() { /* static class */ }

  static final class SpecRunner {
    private final MockSpecReporter reporter;

    public static void main(Suite suite, MockSpecReporter reporter, ExitHandler system) {
      SpecRunner runner = new SpecRunner(reporter);
      runner.run(suite);

      int exitCode = reporter.hasFailingSpecs() ? 1 : 0;
      system.exit(exitCode);
    }

    private SpecRunner(MockSpecReporter reporter) {
      this.reporter = reporter;
    }

    private void run(Suite suite) {
      suite.runSpecs(this.reporter);
    }
  }

  static final class Suite {
    private final List<MockSpec> specs;

    public Suite(MockSpec... specs) {
      this.specs = Stream.of(specs).collect(Collectors.toList());
    }

    public void runSpecs(MockSpecReporter reporter) {
      for(MockSpec spec : specs) {
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
