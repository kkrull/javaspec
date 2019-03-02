package info.javaspec.console;

import info.javaspec.lang.lambda.FunctionalDslDeclaration;
import info.javaspec.lang.lambda.InstanceSpecFinder;

import java.util.List;

class ArgumentParser implements Main.CommandParser {
  private final CommandFactory commandFactory;

  public ArgumentParser(CommandFactory commandFactory) {
    this.commandFactory = commandFactory;
  }

  @Override
  public Command parseCommand(List<String> args) {
    InstanceSpecFinder specFinder = new InstanceSpecFinder(strategy -> {
      FunctionalDslDeclaration.beginDeclaration();
      strategy.declareSpecs();
      return FunctionalDslDeclaration.endDeclaration();
    });

    return this.commandFactory.runSpecsCommand(specFinder, args);
  }

  @FunctionalInterface
  interface CommandFactory {
    Command runSpecsCommand(InstanceSpecFinder finder, List<String> classNames);
  }
}
