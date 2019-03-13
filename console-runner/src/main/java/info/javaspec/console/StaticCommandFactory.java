package info.javaspec.console;

import info.javaspec.lang.lambda.FunctionalDslStrategy;

import java.util.List;

public class StaticCommandFactory implements ArgumentParser.CommandFactory {
  @Override
  public Command helpCommand() {
    return new HelpCommand();
  }

  @Override
  public Command runSpecsCommand(List<String> classNames) {
    return new RunSpecsCommand(new FunctionalDslStrategy(classNames));
  }
}
