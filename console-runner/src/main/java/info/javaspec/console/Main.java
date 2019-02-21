package info.javaspec.console;

import info.javaspec.SpecReporter;
import info.javaspec.lang.lambda.InstanceSpecFinder;

public class Main {
  private final SpecReporter reporter;
  private final ExitHandler system;

  public static void main(String... args) {
    CommandParser parser = new ArgumentParser(new InstanceSpecFinder());
    Command command = parser.parseCommand(args);

    Main cli = new Main(new ConsoleReporter(System.out), System::exit);
    cli.runCommand(command);
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
    Command parseCommand(String[] args);
  }
}
