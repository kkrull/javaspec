package info.javaspec;

public interface SpecReporter {
  boolean hasFailingSpecs();

  void runStarting();

  void specStarting(Spec spec);

  void specFailed(Spec spec);

  void specPassed(Spec spec);

  void suiteStarting(Suite suite);

  void runFinished();
}
