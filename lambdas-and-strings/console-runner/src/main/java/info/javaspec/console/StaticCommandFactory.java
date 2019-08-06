package info.javaspec.console;

import info.javaspec.RunObserver;
import info.javaspec.console.help.DetailedHelpCommand;
import info.javaspec.console.help.HelpCommand;
import info.javaspec.console.help.HelpObserver;
import info.javaspec.lang.lambda.FunctionalDslFactory;
import info.javaspec.lang.lambda.RunSpecsCommand;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
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
  public Command runSpecsCommand(RunObserver observer, String specClassPath, List<String> classNames) {
    ClassLoader specClassLoader = makeClassLoader(specClassPath);
    return new RunSpecsCommand(
      new FunctionalDslFactory(specClassLoader, classNames),
      observer
    );
  }

  private ClassLoader makeClassLoader(String specClassPath) {
    URI uri = new File(specClassPath).toURI();
    URL localPathAsUrl;
    try {
      localPathAsUrl = uri.toURL();
    } catch(MalformedURLException e) {
      throw new RuntimeException("Failed to parse URL", e);
    }

    return new URLClassLoader(new URL[]{ localPathAsUrl });
  }
}
