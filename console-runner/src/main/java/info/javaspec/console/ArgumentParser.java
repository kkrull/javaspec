package info.javaspec.console;

import info.javaspec.lang.lambda.InstanceSpecFinder;

public class ArgumentParser implements Main.CommandParser {
  private final InstanceSpecFinder specFinder;

  public ArgumentParser(InstanceSpecFinder specFinder) {
    this.specFinder = specFinder;
  }

  @Override
  public Command parseCommand(String[] args) {
    return new RunSpecsCommand(this.specFinder, args);
  }
}
