package info.javaspec.spec;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

public final class FakeSpec extends Spec {
  private FakeSpec(String id) {
    super(id);
  }

  @Override
  public void addDescriptionTo(Description suite) { }

  @Override
  public Description getDescription() { throw new UnsupportedOperationException(); }

  @Override
  public boolean isIgnored() { return false; }

  @Override
  public void run() { }

  @Override
  public void run(RunNotifier notifier) {
    throw new UnsupportedOperationException();
  }
}
