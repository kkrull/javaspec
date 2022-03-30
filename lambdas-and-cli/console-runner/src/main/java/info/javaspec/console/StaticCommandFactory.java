package info.javaspec.console;

import com.beust.jcommander.JCommander;
import info.javaspec.RunObserver;
import info.javaspec.console.help.HelpCommand;
import info.javaspec.console.help.HelpObserver;
import info.javaspec.lang.lambda.FunctionalDslFactory;
import info.javaspec.lang.lambda.RunSpecsCommand;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Optional;

public class StaticCommandFactory implements CommandFactory {
  @Override
  public Command helpCommand(HelpObserver observer, JCommander jCommander) {
    return new HelpCommand(observer, jCommander);
  }

  @Override
  public Command runSpecsCommand(RunObserver observer, List<URL> specClassPath, List<String> classNames) {
    return Optional.of(specClassPath)
      .map(classPathAsList -> classPathAsList.toArray(new URL[0]))
      .map(URLClassLoader::new)
      .map(specClassLoader -> new FunctionalDslFactory(specClassLoader, classNames))
      .map(specFactory -> new RunSpecsCommand(specFactory, observer))
      .get();
  }
}
