package info.javaspec.console;

import info.javaspec.lang.lambda.InstanceSpecFinder;

public class Main {
  private final ExitHandler system;

  public static void main(String... args) {
    CommandParser parser = new ArgumentParser(
      new InstanceSpecFinder(),
      new ConsoleReporter(System.out)
    );

    Command command = parser.parseCommand(args);
    Main cli = new Main(System::exit);
    cli.runCommand(command);
  }

  Main(ExitHandler system) {
    this.system = system;
  }

  void runCommand(Command command) {
    int code = command.run();
    system.exit(code);
  }

  @FunctionalInterface
  interface CommandParser {
    Command parseCommand(String[] args);
  }
}
