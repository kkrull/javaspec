package info.javaspec.console;

import info.javaspec.SpecReporter;
import info.javaspec.Suite;
import info.javaspec.lang.lambda.InstanceSpecFinder;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//CommandLine#parseArgs(String... args) -> Runner [load stuff]
//Runner#run -> exitCode [run stuff]
//ExitHandler#exit(exitCode) [exit]
public final class Runner {
  public static void main(String... args) { //TODO KDK: Test
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

  private static Class<?> loadClass(String className) { //TODO KDK: Test
    try {
      return Class.forName(className);
    } catch(ClassNotFoundException e) {
      throw new RuntimeException("Failed to load class", e);
    }
  }

  static void main(Suite suite, SpecReporter reporter, ExitHandler system) {
    Runner.run(suite, reporter);

    int exitCode = reporter.hasFailingSpecs() ? 1 : 0;
    system.exit(exitCode);
  }

  private static void run(Suite suite, SpecReporter reporter) {
    reporter.runStarting();
    suite.runSpecs(reporter);
    reporter.runFinished();
  }

  @FunctionalInterface
  interface ExitHandler {
    void exit(int code);
  }
}
