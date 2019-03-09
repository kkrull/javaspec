package info.javaspec.console;

import info.javaspec.lang.lambda.FunctionalDsl;
import info.javaspec.lang.lambda.InstanceSpecFinder;

import java.util.List;

public class StaticCommandFactory implements ArgumentParser.CommandFactory {
  @Override
  public Command helpCommand() {
    return new HelpCommand();
  }

  @Override
  public Command runSpecsCommand(List<String> classNames) {
    InstanceSpecFinder specFinder = new InstanceSpecFinder(strategy -> {
      FunctionalDsl.openScope();
      strategy.declareSpecs();
      return FunctionalDsl.closeScope();
    });

    return new RunSpecsCommand(specFinder, classNames);
  }
}
