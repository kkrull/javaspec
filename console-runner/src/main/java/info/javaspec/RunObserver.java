package info.javaspec;

public interface RunObserver {
  void collectionStarting(SpecCollection collection);

  boolean hasFailingSpecs();

  void runStarting();

  void specStarting(Spec spec);

  void specFailed(Spec spec);

  void specPassed(Spec spec);

  void runFinished();
}
