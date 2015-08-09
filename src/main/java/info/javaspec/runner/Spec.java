package info.javaspec.runner;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

abstract class Spec {
  private final String id;

  protected Spec(String id) {
    this.id = id;
  }

  protected String getId() { return id; }
  public abstract void addDescriptionTo(Description suite);
  public abstract boolean isIgnored();
  public abstract void run() throws Exception;
}
