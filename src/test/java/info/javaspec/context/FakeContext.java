package info.javaspec.context;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

public final class FakeContext extends Context {
  private final long numSpecs;
  private final Description description;

  public static FakeContext withDescription(Description description) {
    return new FakeContext("withDescription", 1, description);
  }

  public static FakeContext withNoSpecs(String id) {
    return new FakeContext(id, 0, suiteDescription(id));
  }

  public static FakeContext withNumSpecs(long numSpecs) {
    return new FakeContext("withNumSpecs", numSpecs, suiteDescription("withNumSpecs"));
  }

  public static FakeContext withNumSpecs(String id, long numSpecs) {
    return new FakeContext(id, numSpecs, suiteDescription(id));
  }

  private static Description suiteDescription(String id) {
    return Description.createSuiteDescription(id, id);
  }

  private FakeContext(String id, long numSpecs, Description description) {
    super(id);
    this.numSpecs = numSpecs;
    this.description = description;
  }

  @Override
  public Description getDescription() { return description; }

  @Override
  public boolean hasSpecs() { return numSpecs > 0; }

  @Override
  public long numSpecs() { return numSpecs; }

  @Override
  public void run(RunNotifier notifier) { throw new UnsupportedOperationException(); }
}
