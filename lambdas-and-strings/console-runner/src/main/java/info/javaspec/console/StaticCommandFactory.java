package info.javaspec.console;

import info.javaspec.RunObserver;
import info.javaspec.console.help.DetailedHelpCommand;
import info.javaspec.console.help.HelpCommand;
import info.javaspec.console.help.HelpObserver;
import info.javaspec.lang.lambda.FunctionalDslFactory;
import info.javaspec.lang.lambda.RunSpecsCommand;

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
    ClassLoader specClassLoader = FunctionalDslFactory.class.getClassLoader();
    return new RunSpecsCommand(
      new FunctionalDslFactory(specClassLoader, classNames),
      observer
    );
  }
}
