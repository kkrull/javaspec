package info.javaspec.console;

import com.beust.jcommander.JCommander;
import info.javaspec.RunObserver;
import info.javaspec.console.help.HelpCommand;
import info.javaspec.console.help.HelpObserver;
import info.javaspec.lang.lambda.FunctionalDslFactory;
import info.javaspec.lang.lambda.RunSpecsCommand;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.List;

public class StaticCommandFactory implements CommandFactory {
  @Override
  public Command helpCommand(HelpObserver observer, JCommander jCommander) {
    return new HelpCommand(observer, jCommander);
  }

  @Override
  public Command runSpecsCommand(RunObserver observer, URL specClassPath, List<String> classNames) {
    return this.runSpecsCommand(observer, Collections.singletonList(specClassPath), classNames);
  }

  public Command runSpecsCommand(RunObserver observer, List<URL> specClassPath, List<String> classNames) {
    URL[] specClassPathArray = specClassPath.toArray(new URL[0]);
    ClassLoader specClassLoader = new URLClassLoader(specClassPathArray);
    return new RunSpecsCommand(
      new FunctionalDslFactory(specClassLoader, classNames),
      observer
    );
  }
}
