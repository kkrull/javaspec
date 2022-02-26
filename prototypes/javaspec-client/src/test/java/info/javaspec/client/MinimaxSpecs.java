package info.javaspec.client;

import info.javaspec.api.JavaSpec;
import info.javaspec.api.SpecClass;
import org.junit.platform.commons.annotation.Testable;

import static org.junit.jupiter.api.Assertions.*;

public class MinimaxSpecs implements SpecClass {
  public void declareSpecs(JavaSpec javaspec) {
    javaspec.it("scores a game ending in a draw as 0", () -> {
      Minimax subject = new Minimax();
      GameState game = new GameWithKnownState(true);
      assertEquals(0, subject.score(game));
    });
  }

  public static class Minimax {
    public int score(GameState game) {
      if(game.isOver()) {
        return 0;
      }

      return 9999;
    }
  }

  public static class GameWithKnownState implements GameState {
    private final boolean isOver;

    public GameWithKnownState(boolean isOver) {
      this.isOver = isOver;
    }

    public boolean isOver() {
      return this.isOver;
    }
  }

  public interface GameState {
    boolean isOver();
  }
}