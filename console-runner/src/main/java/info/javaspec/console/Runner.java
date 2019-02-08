package info.javaspec.console;

import info.javaspec.SpecReporter;
import info.javaspec.Suite;
import info.javaspec.lang.lambda.InstanceSpecFinder;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Runner {
  private final SpecReporter reporter;

  public static void main(String... args) {
    List<Class<?>> specClasses = Stream.of(args)
      .map(Runner::loadClass)
      .collect(Collectors.toList());

    InstanceSpecFinder finder = new InstanceSpecFinder();
    main(
      finder.findSpecs(specClasses),
      new ConsoleReporter(System.out),
      System::exit
    );
  }

  private static Class<?> loadClass(String className) {
    try {
      return Class.forName(className);
    } catch(ClassNotFoundException e) {
      throw new RuntimeException("Failed to load class", e);
    }
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
