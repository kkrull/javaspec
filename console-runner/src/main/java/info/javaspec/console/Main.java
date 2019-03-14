package info.javaspec.console;

import info.javaspec.Reporter;

import java.util.Arrays;
import java.util.List;

public final class Main {
  private final Reporter reporter;
  private final ExitHandler system;

  public static void main(String... args) {
    main(
      new ConsoleReporter(System.out),
      System::exit,
      args
    );
  }

  static void main(Reporter reporter, ExitHandler system, String... args) {
    CommandParser parser = new ArgumentParser(new StaticCommandFactory());
    Main cli = new Main(reporter, system);
    cli.runCommand(parser.parseCommand(Arrays.asList(args)));
  }

  Main(Reporter reporter, ExitHandler system) {
    this.reporter = reporter;
    this.system = system;
  }

  void runCommand(Command command) {
    int code = command.run(this.reporter);
    this.system.exit(code);
  }

  @FunctionalInterface
  interface CommandParser {
    Command parseCommand(List<String> args);
  }

  @FunctionalInterface
  interface ExitHandler {
    void exit(int statusCode);
  }
}
