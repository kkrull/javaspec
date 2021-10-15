package info.javaspec.engine;

public class LambdaSpec {
  private final String behavior;
  private final Executable verification;

  public LambdaSpec(String behavior, Executable verification) {
    this.behavior = behavior;
    this.verification = verification;
  }

  public void run() {
    this.verification.execute();
  }

  @FunctionalInterface
  public interface Executable {
    void execute();
  }
}
