package info.javaspec.console;

import info.javaspec.Reporter;
import info.javaspec.RunObserver;

import java.util.List;

final class ArgumentParser implements Main.CommandParser {
  private final CommandFactory factory;
  private final Reporter reporter;

  public ArgumentParser(CommandFactory factory, Reporter reporter) {
    this.factory = factory;
    this.reporter = reporter;
  }

  @Override
  public Command parseCommand(List<String> args) {
    if(args.isEmpty())
      return this.factory.helpCommand();

    String command = args.get(0);
    switch(command) {
      case "help":
        return this.factory.helpCommand();

      case "run":
        List<String> classNames = args.subList(1, args.size());
        return this.factory.runSpecsCommand(this.reporter, classNames);

      default:
        throw InvalidCommand.named(command);
    }
  }

  interface CommandFactory {
    Command helpCommand();

    Command runSpecsCommand(RunObserver observer, List<String> classNames);
  }

  static final class InvalidCommand extends RuntimeException {
    public static InvalidCommand named(String command) {
      String message = String.format("Invalid command: %s", command);
      return new InvalidCommand(message);
    }

    private InvalidCommand(String message) {
      super(message);
    }
  }
}
