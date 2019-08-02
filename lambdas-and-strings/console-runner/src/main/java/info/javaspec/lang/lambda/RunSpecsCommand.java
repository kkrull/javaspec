package info.javaspec.lang.lambda;

import info.javaspec.RunObserver;
import info.javaspec.SpecCollection;
import info.javaspec.console.Command;
import info.javaspec.lang.lambda.SpecCollectionFactory;

public final class RunSpecsCommand implements Command {
  private final SpecCollectionFactory factory;
  private final RunObserver observer;

  public RunSpecsCommand(SpecCollectionFactory factory, RunObserver observer) {
    this.factory = factory;
    this.observer = observer;
  }

  @Override
  public int run() {
    SpecCollection rootCollection;
    try {
      rootCollection = this.factory.declareSpecs();
    } catch(Exception e) {
      return 2;
    }

    rootCollection.runSpecs(this.observer);
    return this.observer.hasFailingSpecs() ? 1 : 0;
  }

  @Override
  public Result runResult() {
    SpecCollection rootCollection;
    try {
      rootCollection = this.factory.declareSpecs();
    } catch(Exception e) {
      return new Result(2, e);
    }

    rootCollection.runSpecs(this.observer);
    return this.observer.hasFailingSpecs() ? new Result(1) : new Result(0);
  }
}
