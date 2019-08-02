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
      //TODO KDK: Return the exception along with the error code
//      e.printStackTrace();
      return 2;
    }

    rootCollection.runSpecs(this.observer);
    return this.observer.hasFailingSpecs() ? 1 : 0;
  }
}
