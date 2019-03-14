package info.javaspec.console;

import info.javaspec.SpecCollection;
import info.javaspec.RunObserver;
import info.javaspec.lang.lambda.SpecCollectionFactory;

final class RunSpecsCommand implements Command {
  private final SpecCollectionFactory factory;
  private final RunObserver observer;

  public RunSpecsCommand(SpecCollectionFactory factory, RunObserver observer) {
    this.factory = factory;
    this.observer = observer;
  }

  @Override
  public int run(RunObserver observer) {
    SpecCollection rootCollection;
    try {
      rootCollection = this.factory.declareSpecs();
    } catch(Exception e) {
      return 2;
    }

    observer.runStarting();
    rootCollection.runSpecs(observer);
    observer.runFinished();
    return observer.hasFailingSpecs() ? 1 : 0;
  }
}
