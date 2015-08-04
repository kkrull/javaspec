package info.javaspec.runner;

import org.junit.runner.notification.RunNotifier;

public final class FakeSpec extends Spec {
  private FakeSpec(String id) {
    super(id);
  }

  @Override
  public boolean isIgnored() { return false; }

  @Override
  public void run(RunNotifier notifier) { }

  @Override
  public void run() { }
}
