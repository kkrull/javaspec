package info.javaspec.runner;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

abstract class Context {
  private final String id;
  private final String displayName;

  protected Context(String id, String displayName) {
    this.id = id;
    this.displayName = displayName;
  }

  public String getId() { return id; }
  public String getDisplayName() { return displayName; }

  public abstract Description getDescription();
  public abstract boolean hasSpecs();
  public abstract long numSpecs();
  public abstract void run(RunNotifier notifier);

}
