package info.javaspec.console;

import info.javaspec.lang.lambda.InstanceSpecFinder;

import java.util.List;

class ArgumentParser implements Main.CommandParser {
  private final CommandFactory commandFactory;

  public ArgumentParser(CommandFactory commandFactory) {
    this.commandFactory = commandFactory;
  }

  @Override
  public Command parseCommand(List<String> args) {
    return this.commandFactory.runSpecsCommand(
      new InstanceSpecFinder(),
      args
    );
  }

  @FunctionalInterface
  interface CommandFactory {
    Command runSpecsCommand(InstanceSpecFinder finder, List<String> classNames);
  }
}
