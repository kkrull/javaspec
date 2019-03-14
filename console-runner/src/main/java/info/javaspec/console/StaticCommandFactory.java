package info.javaspec.console;

import info.javaspec.lang.lambda.FunctionalDslFactory;

import java.util.List;

public class StaticCommandFactory implements ArgumentParser.CommandFactory {
  @Override
  public Command helpCommand() {
    return new HelpCommand();
  }

  @Override
  public Command runSpecsCommand(List<String> classNames) {
    return new RunSpecsCommand(new FunctionalDslFactory(classNames));
  }
}
