package info.javaspec.console;

import info.javaspec.SpecReporter;

public class Main {
  private final SpecReporter reporter;
  private final ExitHandler system;

  public static void main(String... args) {
    main(
      new ConsoleReporter(System.out),
      System::exit,
      args
    );
  }

  static void main(SpecReporter reporter, ExitHandler system, String... args) {
    CommandParser parser = new ArgumentParser(RunSpecsCommand::new);
    Main cli = new Main(reporter, system);
    cli.runCommand(parser.parseCommand(args));
  }

  Main(SpecReporter reporter, ExitHandler system) {
    this.reporter = reporter;
    this.system = system;
  }

  void runCommand(Command command) {
    int code = command.run(this.reporter);
    this.system.exit(code);
  }

  @FunctionalInterface
  interface CommandParser {
    Command parseCommand(String... args);
  }
}
