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

  public SpecContainer declareSpecs() {
    //TODO KDK: [3] Extract JupiterJavaSpec instance with #describe and #it to create SpecContainer and LambdaSpec
    SpecContainer container = new SpecContainer();
    container.addSpec(new LambdaSpec("greets the world", () -> GreeterSpecs.incrementRunCount()));
    return container;
  }
}
