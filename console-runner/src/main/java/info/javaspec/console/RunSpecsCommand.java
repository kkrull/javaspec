package info.javaspec.console;

import info.javaspec.SpecCollection;
import info.javaspec.SpecReporter;
import info.javaspec.lang.lambda.SpecCollectionFactory;

final class RunSpecsCommand implements Command {
  private final SpecCollectionFactory factory;

  public RunSpecsCommand(SpecCollectionFactory factory) {
    this.factory = factory;
  }

  @Override
  public int run(SpecReporter reporter) {
    SpecCollection rootCollection;
    try {
      rootCollection = this.factory.declareSpecs();
    } catch(Exception e) {
      return 2;
    }

    reporter.runStarting();
    rootCollection.runSpecs(reporter);
    reporter.runFinished();
    return reporter.hasFailingSpecs() ? 1 : 0;
  }
}
