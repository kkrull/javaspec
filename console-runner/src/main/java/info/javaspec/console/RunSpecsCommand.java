package info.javaspec.console;

import info.javaspec.SpecReporter;
import info.javaspec.Suite;
import info.javaspec.lang.lambda.InstanceSpecFinder;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final class RunSpecsCommand implements Command {
  private final InstanceSpecFinder finder;
  private final SpecReporter reporter;
  private final String[] specClassNames;

  public RunSpecsCommand(InstanceSpecFinder specFinder, SpecReporter reporter, String... specClassNames) {
    this.finder = specFinder;
    this.specClassNames = specClassNames;
    this.reporter = reporter;
  }

  @Override
  public int run() {
    Suite suite = loadSpecs();
    runSpecs(suite);
    return this.reporter.hasFailingSpecs() ? 1 : 0;
  }

  private Suite loadSpecs() {
    List<Class<?>> specClasses = Stream.of(this.specClassNames)
      .map(RunSpecsCommand::loadClass)
      .collect(Collectors.toList());

    return this.finder.findSpecs(specClasses);
  }

  private static Class<?> loadClass(String className) {
    try {
      return Class.forName(className);
    } catch(ClassNotFoundException e) {
      throw new RuntimeException("Failed to load class", e);
    }
  }

  private void runSpecs(Suite suite) {
    this.reporter.runStarting();
    suite.runSpecs(this.reporter);
    this.reporter.runFinished();
  }
}
