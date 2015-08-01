package info.javaspec.runner;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

abstract class Context {
  private final String id;

  protected Context(String id) {
    this.id = id;
  }

  public String getId() { return id; }

  public abstract Description getDescription();
  public abstract boolean hasSpecs();
  public abstract long numSpecs();
  public abstract void run(RunNotifier notifier);
}
