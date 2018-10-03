package info.javaspec;

import info.javaspec.console.ConsoleReporter;

public class ConsoleRunner {
  public static void main(String... args) throws Exception {
    Class<?> specClass = Class.forName(args[0]);
    main(
      new GreeterSuite(specClass),
      new ConsoleReporter(System.out),
      System::exit
    );
  }

  static void main(SpecSuite suite, SpecObserver reporter, ExitHandler system) {
    reporter.testRunStarted();
    SuiteResult result = suite.run(reporter);
    reporter.testRunFinished();
    result.doExit(system);
  }

  private static final class GreeterSuite implements SpecSuite {
    public GreeterSuite(Class<?> specClass) { }

    @Override
    public SuiteResult run(SpecObserver observer) {
      System.out.println("Greeter"); //TODO KDK: fireSuiteStarting, fireSpecClassStarting
      System.out.println("  says hello: PASS");
      return SuiteResult.ALL_SPECS_PASSED;
    }
  }
}
