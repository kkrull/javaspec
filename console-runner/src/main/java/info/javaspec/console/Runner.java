package info.javaspec.console;

import info.javaspec.SpecReporter;
import info.javaspec.Suite;

public final class Runner {
  private final SpecReporter reporter;

  public static void main(String... args) throws Exception {
    Class<?> specClass = Class.forName(args[0]);
    InstanceSpecFinder finder = new InstanceSpecFinder();

    main(
      finder.findSpecs(specClass),
      new ConsoleReporter(System.out),
      System::exit
    );
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

  @FunctionalInterface
  interface ExitHandler {
    void exit(int code);
  }
}
