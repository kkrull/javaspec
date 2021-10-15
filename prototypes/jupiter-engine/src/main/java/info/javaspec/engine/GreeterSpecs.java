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

  public JupiterSpecContainer declareSpecs() {
    //TODO KDK: [3] Extract JupiterJavaSpec instance with #describe and #it to create SpecContainer and JupiterSpec
    JupiterSpecContainer container = new JupiterSpecContainer();
    container.addSpec(new JupiterSpec("greets the world", () -> GreeterSpecs.incrementRunCount()));
    return container;
  }
}
