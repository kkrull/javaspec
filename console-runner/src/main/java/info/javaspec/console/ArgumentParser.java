package info.javaspec.console;

import info.javaspec.lang.lambda.InstanceSpecFinder;
import info.javaspec.lang.lambda.SpecDeclaration;

import java.util.List;

class ArgumentParser implements Main.CommandParser {
  private final CommandFactory commandFactory;

  public ArgumentParser(CommandFactory commandFactory) {
    this.commandFactory = commandFactory;
  }

  @Override
  public Command parseCommand(List<String> args) {
    InstanceSpecFinder specFinder = new InstanceSpecFinder(strategy -> {
      SpecDeclaration.beginDeclaration();
      strategy.declareSpecs();
      return SpecDeclaration.endDeclaration();
    });

    return this.commandFactory.runSpecsCommand(specFinder, args);
  }

  @FunctionalInterface
  interface CommandFactory {
    Command runSpecsCommand(InstanceSpecFinder finder, List<String> classNames);
  }
}
