package info.javaspec;

public interface SpecReporter {
  void collectionStarting(Suite collection);

  boolean hasFailingSpecs();

  void runStarting();

  void specStarting(Spec spec);

  void specFailed(Spec spec);

  void specPassed(Spec spec);

  void runFinished();
}
