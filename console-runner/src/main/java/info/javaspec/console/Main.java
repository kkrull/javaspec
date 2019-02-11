package info.javaspec.console;

public class Main {
  private final CommandParser parser;
  private final ExitHandler system;

  public static void main(String... args) {
    CommandParser parser = new ArgumentParser();
    Main cli = new Main(parser, System::exit);
    cli.parseAndRunCommand(args);
  }

  Main(CommandParser parser, ExitHandler system) {
    this.parser = parser;
    this.system = system;
  }

  void parseAndRunCommand(String... args) {
    Command command = this.parser.parseCommand(args);
    int code = command.run();
    system.exit(code);
  }

  @FunctionalInterface
  interface CommandParser {
    Command parseCommand(String[] args);
  }

  @FunctionalInterface
  interface Command {
    int run();
  }

  @FunctionalInterface
  interface ExitHandler {
    void exit(int statusCode);
  }
}
