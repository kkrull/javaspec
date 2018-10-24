package info.javaspec.console;

import info.javaspec.SpecReporter;
import info.javaspec.Suite;

public class Runner {
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

  private static final class InstanceSpecFinder {
    public Suite findSpecs(Class<?> specClass) {
      SpecDeclaration.newContext();
      try {
        specClass.newInstance();
      } catch(Exception e) {
        throw SpecDeclarationFailed.whenInstantiating(specClass, e);
      }

      return SpecDeclaration.createSuite();
    }
  }

  private static final class SpecDeclarationFailed extends RuntimeException {
    public static SpecDeclarationFailed whenInstantiating(Class<?> specClass, Exception cause) {
      return new SpecDeclarationFailed(
        String.format("Failed to instantiate spec %s, to declare specs", specClass.getName()),
        cause);
    }

    private SpecDeclarationFailed(String message, Exception cause) {
      super(message, cause);
    }
  }
}
