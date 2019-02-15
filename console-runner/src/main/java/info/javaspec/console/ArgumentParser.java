package info.javaspec.console;

import info.javaspec.SpecReporter;
import info.javaspec.lang.lambda.InstanceSpecFinder;

public class ArgumentParser implements Main.CommandParser {
  private final InstanceSpecFinder specFinder;
  private final SpecReporter reporter;

  public ArgumentParser(InstanceSpecFinder specFinder, SpecReporter reporter) {
    this.specFinder = specFinder;
    this.reporter = reporter;
  }

  @Override
  public Command parseCommand(String[] args) {
    return new RunSpecsCommand(this.specFinder, this.reporter, args);
  }
}
