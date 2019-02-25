package info.javaspec.console;

import info.javaspec.lang.lambda.InstanceSpecFinder;

import java.util.List;

class ArgumentParser implements Main.CommandParser {
  private final RunSpecsCommandFactory newRunSpecsCommand;

  public ArgumentParser(RunSpecsCommandFactory newRunSpecsCommand) {
    this.newRunSpecsCommand = newRunSpecsCommand;
  }

  @Override
  public Command parseCommand(List<String> args) {
    return this.newRunSpecsCommand.make(
      new InstanceSpecFinder(),
      args
    );
  }

  @FunctionalInterface
  interface RunSpecsCommandFactory {
    Command make(InstanceSpecFinder finder, List<String> classNames);
  }
}
