package info.javaspec.console;

import info.javaspec.console.plaintext.PlainTextReporter;

import java.util.Arrays;
import java.util.List;

public final class Main {
  private final Reporter reporter;
  private final ExitHandler system;

  public static void main(String... args) {
    main(
      new PlainTextReporter(System.out),
      System::exit,
      args
    );
  }

  static void main(Reporter reporter, ExitHandler system, String... args) {
    CommandParser parser = new ArgumentParser(new StaticCommandFactory(), reporter);
    Main cli = new Main(reporter, system);
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
    Command parseCommand(List<String> commandThenArguments);
  }

  @FunctionalInterface
  interface ExitHandler {
    void exit(int statusCode);
  }
}
