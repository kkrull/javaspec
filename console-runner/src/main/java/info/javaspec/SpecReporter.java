package info.javaspec;

public interface SpecReporter {
  boolean hasFailingSpecs();

  void runStarting();

  void specStarting(LambdaSpec spec, String description);

  void specFailed(LambdaSpec spec);

  void specPassed(LambdaSpec spec);

  void runFinished();
}
