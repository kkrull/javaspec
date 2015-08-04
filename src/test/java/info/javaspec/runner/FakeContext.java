package info.javaspec.runner;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class FakeContext extends Context {
  public final List<FakeSpec> specs;
  public final List<FakeContext> subcontexts;
  private long numSpecs;
  private Description description;

  public static FakeContext withDescription(Description description) {
    FakeContext context = new FakeContext("Root", 1);
    context.description = description;
    return context;
  }

  public static FakeContext withNumSpecs(String id, long numSpecs) {
    return new FakeContext(id, numSpecs);
  }

  public static FakeContext withSpecs(FakeSpec... specs) {
    return new FakeContext("root", Arrays.asList(specs), new ArrayList<>(0));
  }

  public static FakeContext withNoSpecs(String id) {
    return new FakeContext(id, new ArrayList<>(0), new ArrayList<>(0));
  }

  private FakeContext(String id, long numSpecs) {
    super(id);
    this.specs = new ArrayList<>(0);
    this.subcontexts = new ArrayList<>(0);
    this.numSpecs = numSpecs;
  }

  private FakeContext(String id, List<FakeSpec> specs, List<FakeContext> subcontexts) {
    super(id);
    this.specs = specs;
    this.subcontexts = subcontexts;
    this.numSpecs = this.specs.size();
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
