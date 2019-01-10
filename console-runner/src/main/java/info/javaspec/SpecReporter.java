package info.javaspec;

public interface SpecReporter {
  boolean hasFailingSpecs();

  void runStarting();

  void specStarting(Spec spec, String description); //TODO KDK: Remove description since it's already on Spec

  void specFailed(Spec spec);

  void specPassed(Spec spec);

  void runFinished();
}
