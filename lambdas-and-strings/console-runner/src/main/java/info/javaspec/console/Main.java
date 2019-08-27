package info.javaspec.console;

import java.util.Arrays;
import java.util.List;

public final class Main {
  private final Reporter reporter;
  private final ExitHandler system;

  public static void main(String... args) {
    main(
      new StaticReporterFactory(System.out),
      System::exit,
      args
    );
  }

  static void main(ReporterFactory reporterFactory, ExitHandler system, String... args) {
    CommandParser parser = new ArgumentParser(new StaticCommandFactory(), reporterFactory);
    Main cli = new Main(reporterFactory.plainTextReporter(), system);
    cli.runCommand(parser.parseCommand(Arrays.asList(args)));
  }

  Main(Reporter reporter, ExitHandler system) {
    this.reporter = reporter;
    this.system = system;
  }

  void runCommand(Command command) {
    Result result = command.run();
    result.reportTo(this.reporter);
    this.system.exit(result.exitCode);
  }

  @FunctionalInterface
  interface CommandParser {
    Command parseCommand(List<String> arguments);
  }

  @FunctionalInterface
  interface ExitHandler {
    void exit(int statusCode);
  }
}
