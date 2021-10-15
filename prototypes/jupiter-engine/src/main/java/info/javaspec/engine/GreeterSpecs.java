package info.javaspec.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GreeterSpecs {
  private static int _numTimesRun = 0;

  public static void assertRanOnce() {
    assertEquals(1, _numTimesRun, "Expected GreeterSpecs to have been run once");
  }

  public static void incrementRunCount() {
    _numTimesRun++;
  }

  public GreeterSpecs() {
    System.out.println("[GreeterSpecs::GreeterSpecs]");
  }

  public LambdaSpec getOnlySpec() {
    return new LambdaSpec("greets the world", () -> GreeterSpecs.incrementRunCount());
  }
}
