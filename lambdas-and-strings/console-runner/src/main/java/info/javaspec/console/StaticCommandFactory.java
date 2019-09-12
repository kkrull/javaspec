package info.javaspec.console;

import com.beust.jcommander.JCommander;
import info.javaspec.RunObserver;
import info.javaspec.console.help.DetailedHelpCommand;
import info.javaspec.console.help.HelpCommand;
import info.javaspec.console.help.HelpObserver;
import info.javaspec.lang.lambda.FunctionalDslFactory;
import info.javaspec.lang.lambda.RunSpecsCommand;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

public class StaticCommandFactory implements CommandFactory {
  @Override
  public Command helpCommand(HelpObserver observer, JCommander jCommander) {
    return new HelpCommand(observer, jCommander);
  }

  @Override
  public Command helpCommand(HelpObserver observer, String forCommandNamed) {
    return new DetailedHelpCommand(observer, forCommandNamed);
  }

  @Override
  public Command runSpecsCommand(RunObserver observer, URL specClassPath, List<String> classNames) {
    ClassLoader specClassLoader = new URLClassLoader(new URL[]{ specClassPath });
    return new RunSpecsCommand(
      new FunctionalDslFactory(specClassLoader, classNames),
      observer
    );
  }
}
