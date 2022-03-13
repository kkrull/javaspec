package info.javaspec.context;

import info.javaspec.spec.Spec;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

import java.io.Serializable;

/** A grouping of related specs, which may be further divided into sub-groups (sub-contexts) */
public abstract class Context {
  private final String id;

  protected Context(String id) {
    this.id = id;
  }

  public String getId() { return id; }

  public abstract Description getDescription();
  public Description describeSpec(Serializable specId, String displayName) {
    return Description.createTestDescription(getDescription().getClassName(), displayName, specId);
  }

  public abstract void addSpec(Spec spec);
  public abstract boolean hasSpecs();
  public abstract long numSpecs();

  public abstract void run(RunNotifier notifier);
}
