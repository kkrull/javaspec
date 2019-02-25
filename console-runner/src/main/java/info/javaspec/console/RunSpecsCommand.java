package info.javaspec.console;

import info.javaspec.SpecReporter;
import info.javaspec.Suite;
import info.javaspec.lang.lambda.InstanceSpecFinder;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final class RunSpecsCommand implements Command {
  private final InstanceSpecFinder finder;
  private final List<String> specClassNames;

  public RunSpecsCommand(InstanceSpecFinder specFinder, List<String> specClassNames) {
    this.finder = specFinder;
    this.specClassNames = specClassNames;
  }

  @Override
  public int run(SpecReporter reporter) {
    Suite suite = loadSpecs();
    runSpecs(suite, reporter);
    return reporter.hasFailingSpecs() ? 1 : 0;
  }

  private Suite loadSpecs() {
    List<Class<?>> specClasses = this.specClassNames.stream()
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

  private void runSpecs(Suite suite, SpecReporter reporter) {
    reporter.runStarting();
    suite.runSpecs(reporter);
    reporter.runFinished();
  }
}
