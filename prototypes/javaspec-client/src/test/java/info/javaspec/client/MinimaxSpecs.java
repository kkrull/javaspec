package info.javaspec.client;

import info.javaspec.api.JavaSpec;
import info.javaspec.api.SpecClass;
import org.junit.platform.commons.annotation.Testable;

import static org.junit.jupiter.api.Assertions.*;

public class MinimaxSpecs implements SpecClass {
  public void declareSpecs(JavaSpec javaspec) {
    javaspec.it("scores a game ending in a draw as 0", () -> {
      Minimax subject = new Minimax("Max", "Min");
      GameState game = GameWithKnownState.draw();
      assertEquals(0, subject.score(game));
    });

    javaspec.it("scores a game won by the maximizer as +1", () -> {
      Minimax subject = new Minimax("Max", "Min");
      GameState game = GameWithKnownState.wonBy("Max");
      assertEquals(+1, subject.score(game));
    });

    javaspec.it("scores a game won by the minimizer as -1", () -> {
      Minimax subject = new Minimax("Max", "Min");
      GameState game = GameWithKnownState.wonBy("Min");
      assertEquals(-1, subject.score(game));
    });

    javaspec.it("scores possible next states, from the maximizer's point of view", () -> {
      throw new UnsupportedOperationException("Work here");
    });
  }

  public static class Minimax {
    private final String maximizer;
    private final String minimizer;

    public Minimax(String maximizer, String minimizer) {
      this.maximizer = maximizer;
      this.minimizer = minimizer;
    }

    public int score(GameState game) {
      if(game.hasWon(this.maximizer)) {
        return +1;
      } else if(game.hasWon(this.minimizer)) {
        return -1;
      } else if(game.isOver()) {
        return 0;
      }

      return 9999;
    }
  }

  public static class GameWithKnownState implements GameState {
    private final boolean isOver;
    private final String winner;

    public static GameWithKnownState draw() {
      return new GameWithKnownState(true, null);
    }

    public static GameWithKnownState wonBy(String winner) {
      return new GameWithKnownState(true, winner);
    }

    private GameWithKnownState(boolean isOver, String winner) {
      this.isOver = isOver;
      this.winner = winner;
    }

    public boolean hasWon(String player) {
      return player.equals(this.winner);
    }

    public boolean isOver() {
      return this.isOver;
    }
  }

  public interface GameState {
    boolean hasWon(String player);
    boolean isOver();
  }
}