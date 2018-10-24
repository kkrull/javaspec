package info.javaspec.console;

import info.javaspec.SpecReporter;
import info.javaspec.Suite;

public class Runner {
  private final SpecReporter reporter;

  public static void main(String... args) throws Exception {
    //TODO KDK: Start making new implementations of Suite, SpecReporter, etc...
  }

  static void main(Suite suite, SpecReporter reporter, ExitHandler system) {
    Runner runner = new Runner(reporter);
    runner.run(suite);

    int exitCode = reporter.hasFailingSpecs() ? 1 : 0;
    system.exit(exitCode);
  }

  private Runner(SpecReporter reporter) {
    this.reporter = reporter;
  }

  private void run(Suite suite) {
    this.reporter.runStarting();
    suite.runSpecs(this.reporter);
    this.reporter.runFinished();
  }
}
