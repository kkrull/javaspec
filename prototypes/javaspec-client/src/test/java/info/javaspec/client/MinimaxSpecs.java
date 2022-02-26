package info.javaspec.client;

import info.javaspec.api.JavaSpec;
import info.javaspec.api.SpecClass;
import org.junit.platform.commons.annotation.Testable;

import static org.junit.jupiter.api.Assertions.*;

public class MinimaxSpecs implements SpecClass {
  public void declareSpecs(JavaSpec javaspec) {
    javaspec.it("scores a game ending in a draw as 0", () -> {
      Minimax subject = new Minimax();
      assertEquals(0, subject.score());
    });
  }

  public static class Minimax {
    public int score() {
      return 9999;
    }
  }
}