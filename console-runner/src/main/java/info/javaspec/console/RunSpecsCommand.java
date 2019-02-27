package info.javaspec.console;

import info.javaspec.SpecReporter;
import info.javaspec.Suite;
import info.javaspec.lang.lambda.InstanceSpecFinder;

import java.util.LinkedList;
import java.util.List;

final class RunSpecsCommand implements Command {
  private final InstanceSpecFinder finder;
  private final List<String> specClassNames;

  public RunSpecsCommand(InstanceSpecFinder specFinder, List<String> specClassNames) {
    this.finder = specFinder;
    this.specClassNames = specClassNames;
  }

  @Override
  public int run(SpecReporter reporter) {
    //TODO KDK: Refactor -- this can either produce Right<List<Class<?>>> or Left<CommandResult { badClassName: string }>
    List<Class<?>> specClasses = new LinkedList<>();
    for(String className : this.specClassNames) {
      try {
        specClasses.add(Class.forName(className));
      } catch(ClassNotFoundException e) {
        return 2;
      }
    }

    Suite suite = this.finder.findSpecs(specClasses);
    reporter.runStarting();
    suite.runSpecs(reporter);
    reporter.runFinished();
    return reporter.hasFailingSpecs() ? 1 : 0;
  }
}
