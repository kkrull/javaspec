package info.javaspec.console;

import info.javaspec.RunObserver;
import info.javaspec.lang.lambda.FunctionalDslFactory;

import java.util.List;

public class StaticCommandFactory implements ArgumentParser.CommandFactory {
  @Override
  public Command helpCommand(HelpObserver observer) {
    return new HelpCommand(observer);
  }

  @Override
  public Command helpCommand(HelpObserver observer, String forCommandNamed) {
    return new DetailedHelpCommand(observer, forCommandNamed);
  }

  @Override
  public Command runSpecsCommand(RunObserver observer, List<String> classNames) {
    return new RunSpecsCommand(new FunctionalDslFactory(classNames), observer);
  }
}
