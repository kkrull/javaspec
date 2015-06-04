package info.javaspec.runner.ng;

public class LambdaSpec extends Spec {
  public LambdaSpec(String id, String displayName) {
    super(id, displayName);
  }

  @Override
  public boolean isIgnored() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void run() {
    throw new UnsupportedOperationException();
  }
}
