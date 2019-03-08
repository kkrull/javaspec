package info.javaspec.console;

import info.javaspec.lang.lambda.FunctionalDsl;
import info.javaspec.lang.lambda.InstanceSpecFinder;

import java.util.List;

final class ArgumentParser implements Main.CommandParser {
  private final CommandFactory commandFactory;

  public ArgumentParser(CommandFactory commandFactory) {
    this.commandFactory = commandFactory;
  }

  @Override
  public Command parseCommand(List<String> args) {
    InstanceSpecFinder specFinder = new InstanceSpecFinder(strategy -> {
      FunctionalDsl.openScope();
      strategy.declareSpecs();
      return FunctionalDsl.closeScope();
    });

    List<String> classNames = args.isEmpty() ? args : args.subList(1, args.size());
    return this.commandFactory.runSpecsCommand(specFinder, classNames);
  }

  @FunctionalInterface
  interface CommandFactory {
    Command runSpecsCommand(InstanceSpecFinder finder, List<String> classNames);
  }
}
