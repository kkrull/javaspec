package info.javaspec;

public interface SpecReporter {
  boolean hasFailingSpecs();

  void runStarting();

  void specStarting(Spec spec, String description);

  void specFailed(Spec spec); //TODO KDK: Remove Spec parameter from each of these

  void specPassed(Spec spec);

  void runFinished();
}
