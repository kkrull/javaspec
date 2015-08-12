package info.javaspec.spec;

import org.junit.runner.Description;

public final class FakeSpec extends Spec {
  private FakeSpec(String id) {
    super(id);
  }

  @Override
  public void addDescriptionTo(Description suite) { }

  @Override
  public boolean isIgnored() { return false; }

  @Override
  public void run() { }
}
