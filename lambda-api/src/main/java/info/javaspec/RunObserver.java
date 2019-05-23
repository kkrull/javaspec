package info.javaspec;

public interface RunObserver {
  void beginCollection(SpecCollection collection);

  void endCollection(SpecCollection collection);

  boolean hasFailingSpecs();

  void runStarting();

  void specStarting(Spec spec);

  void specFailed(Spec spec); //TODO KDK: Remove

  void specFailed(Spec spec, AssertionError error);

  void specFailed(Spec spec, Exception exception);

  void specPassed(Spec spec);

  void runFinished();
}
