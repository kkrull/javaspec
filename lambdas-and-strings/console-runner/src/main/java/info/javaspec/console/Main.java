package info.javaspec.console;

import info.javaspec.console.plaintext.PlainTextReporter;

import java.util.Arrays;
import java.util.List;

public final class Main {
  private final Reporter reporter;
  private final ExitHandler system;

  public static void main(String... args) {
    PlainTextReporter singletonReporter = new PlainTextReporter(System.out);
    main(
      () -> singletonReporter,
      System::exit,
      args
    );
  }

  static void main(ReporterFactory reporterFactory, ExitHandler system, String... args) {
    CommandParser parser = new ArgumentParser(new StaticCommandFactory(), reporterFactory);
    Main cli = new Main(parser.reporter(), system);
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

  interface CommandParser {
    Command parseCommand(List<String> commandThenArguments);

    Reporter reporter();
  }

  @FunctionalInterface
  interface ExitHandler {
    void exit(int statusCode);
  }
}
