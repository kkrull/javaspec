package info.javaspec.console;

import java.util.List;

final class ArgumentParser implements Main.CommandParser {
  private final CommandFactory factory;

  public ArgumentParser(CommandFactory factory) {
    this.factory = factory;
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
        return this.factory.runSpecsCommand(classNames);

      default:
        throw InvalidCommand.named(command);
    }
  }

  interface CommandFactory {
    Command helpCommand();

    Command runSpecsCommand(List<String> classNames);
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
