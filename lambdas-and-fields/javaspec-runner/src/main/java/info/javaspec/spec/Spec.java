package info.javaspec.spec;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

public abstract class Spec {
  private final String id;

  protected Spec(String id) {
    this.id = id;
  }

  protected String getId() { return id; }
  public abstract Description getDescription();
  public abstract void addDescriptionTo(Description suite);
  public abstract void run(RunNotifier notifier);
}
