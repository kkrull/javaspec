package info.javaspec.console;

import java.util.List;

final class ArgumentParser implements Main.CommandParser {
  private final CommandFactory commandFactory;

  public ArgumentParser(CommandFactory commandFactory) {
    this.commandFactory = commandFactory;
  }

  @Override
  public Command parseCommand(List<String> args) {
    List<String> classNames = args.isEmpty() ? args : args.subList(1, args.size());
    return this.commandFactory.runSpecsCommand(classNames);
  }

  interface CommandFactory {
    Command runSpecsCommand(List<String> classNames);
  }
}
