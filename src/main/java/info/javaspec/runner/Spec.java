package info.javaspec.runner;

import org.junit.runner.notification.RunNotifier;

abstract class Spec {
  private final String id;

  protected Spec(String id) {
    this.id = id;
  }

  public String getId() { return id; }
  public abstract boolean isIgnored();
  public abstract void run() throws Exception;
  public abstract void run(RunNotifier notifier);
}
