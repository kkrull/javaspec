package info.javaspec;

public interface RunObserver {
  void beginCollection(SpecCollection collection);

  void endCollection(SpecCollection collection);

  boolean hasFailingSpecs();

  void runStarting();

  void specStarting(Spec spec);

  void specFailed(Spec spec);

  void specPassed(Spec spec);

  void runFinished();
}
