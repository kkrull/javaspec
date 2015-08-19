package info.javaspec.context;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

import java.io.Serializable;

public abstract class Context {
  private final String id;

  protected Context(String id) {
    this.id = id;
  }

  public String getId() { return id; }

  public Description describeSpec(Serializable specId, String displayName) {
    return Description.createTestDescription(getDescription().getClassName(), displayName, specId);
  }

  public abstract Description getDescription();
  public abstract boolean hasSpecs();
  public abstract long numSpecs();
  public abstract void run(RunNotifier notifier);
}
