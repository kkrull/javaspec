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
  public Result run() {
    SpecCollection rootCollection;
    try {
      rootCollection = this.factory.declareSpecs();
    } catch(Exception e) {
      return Result.failure(2, e);
    }

    rootCollection.runSpecs(this.observer);
    return this.observer.hasFailingSpecs()
      ? Result.failure(1, "Specs failed")
      : Result.success();
  }
}
