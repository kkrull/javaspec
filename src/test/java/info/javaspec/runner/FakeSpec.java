package info.javaspec.runner;

import org.junit.runner.notification.RunNotifier;

public class FakeSpec extends Spec {
  public static FakeSpec with(String id) {
    return new FakeSpec(id);
  }

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
