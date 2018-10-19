package info.javaspec;

public interface SpecReporter {
  boolean hasFailingSpecs();

  void specFailed(LambdaSpec spec);

  void specPassed(LambdaSpec spec);

  void specStarting(LambdaSpec spec);
}
