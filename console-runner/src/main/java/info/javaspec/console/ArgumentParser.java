package info.javaspec.console;

import info.javaspec.SpecReporter;
import info.javaspec.Suite;
import info.javaspec.lang.lambda.InstanceSpecFinder;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ArgumentParser implements Main.CommandParser {
  @Override
  public Main.Command parseCommand(String[] args) {
    return new RunSpecsCommand(args);
  }

  private static final class RunSpecsCommand implements Main.Command {
    private final String[] args;

    public RunSpecsCommand(String... args) {
      this.args = args;
    }

    @Override
    public int run() {
      List<Class<?>> specClasses = Stream.of(args)
        .map(RunSpecsCommand::loadClass)
        .collect(Collectors.toList());

      InstanceSpecFinder finder = new InstanceSpecFinder();
      Suite suite = finder.findSpecs(specClasses);
      SpecReporter reporter = new ConsoleReporter(System.out);

      reporter.runStarting();
      suite.runSpecs(reporter);
      reporter.runFinished();
      return reporter.hasFailingSpecs() ? 1 : 0;
    }

    private static Class<?> loadClass(String className) {
      try {
        return Class.forName(className);
      } catch(ClassNotFoundException e) {
        throw new RuntimeException("Failed to load class", e);
      }
    }
  }
}
