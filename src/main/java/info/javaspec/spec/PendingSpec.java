package info.javaspec.spec;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

final class PendingSpec extends Spec {
  private final Description testDescription;

  PendingSpec(String id, Description description) {
    super(id);
    this.testDescription = description;
  }

  @Override
  public Description getDescription() {
    return testDescription;
  }

  @Override
  public void addDescriptionTo(Description suite) {
    suite.addChild(testDescription);
  }

  @Override
  public boolean isIgnored() {
    return true;
  }

  @Override
  public void run(RunNotifier notifier) {
    notifier.fireTestIgnored(getDescription());
  }
}
