package info.javaspec.client;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import info.javaspec.api.JavaSpec;
import info.javaspec.api.SpecClass;
import org.junit.platform.commons.annotation.Testable;

import static org.junit.jupiter.api.Assertions.*;

public class MinimaxSpecs implements SpecClass {
  public void declareSpecs(JavaSpec javaspec) {
    javaspec.it("scores a game ending in a draw as 0", () -> {
      Minimax subject = new Minimax("Max", "Min");
      GameState game = GameWithKnownState.draw();
      assertEquals(0, subject.score(game, "Max"));
    });

    javaspec.it("scores a game won by the maximizer as +1", () -> {
      Minimax subject = new Minimax("Max", "Min");
      GameState game = GameWithKnownState.wonBy("Max");
      assertEquals(+1, subject.score(game, "Max"));
    });

    javaspec.it("scores a game won by the minimizer as -1", () -> {
      Minimax subject = new Minimax("Max", "Min");
      GameState game = GameWithKnownState.wonBy("Min");
      assertEquals(-1, subject.score(game, "Max"));
    });

    javaspec.it("given a game that will end in the next move, the maximizer picks the move with the highest score", () -> {
      Minimax subject = new Minimax("Max", "Min");
      GameWithKnownState game = GameWithKnownState.stillGoing();
      game.addKnownState("ThenDraw", GameWithKnownState.draw());
      game.addKnownState("ThenMaxWins", GameWithKnownState.wonBy("Max"));
      assertEquals(+1, subject.score(game, "Max"));
    });
  }

  public static class Minimax {
    private final String maximizer;
    private final String minimizer;

    public Minimax(String maximizer, String minimizer) {
      this.maximizer = maximizer;
      this.minimizer = minimizer;
    }

    public int score(GameState game, String player) {
      if(game.hasWon(this.maximizer)) {
        return +1;
      } else if(game.hasWon(this.minimizer)) {
        return -1;
      } else if(game.isOver()) {
        return 0;
      }

      int maxScore = -999;
      for(String nextMove : game.nextMoves()) {
        GameState nextGame = game.nextState(nextMove);
        int score = score(nextGame, "Max");
        if(score > maxScore) {
          maxScore = score;
        }
      }

      return maxScore;
    }
  }

  public static class GameWithKnownState implements GameState {
    private final boolean isOver;
    private final String winner;
    private final Map<String, GameState> nextMoves;

    public static GameWithKnownState draw() {
      return new GameWithKnownState(true, null);
    }

    public static GameWithKnownState stillGoing() {
      return new GameWithKnownState(false, null);
    }

    public static GameWithKnownState wonBy(String winner) {
      return new GameWithKnownState(true, winner);
    }

    private GameWithKnownState(boolean isOver, String winner) {
      this.isOver = isOver;
      this.winner = winner;
      this.nextMoves = new LinkedHashMap<String, GameState>();
    }

    public void addKnownState(String move, GameState state) {
      this.nextMoves.put(move, state);
    }

    public boolean hasWon(String player) {
      return player.equals(this.winner);
    }

    public boolean isOver() {
      return this.isOver;
    }

    public List<String> nextMoves() {
      return new ArrayList<>(this.nextMoves.keySet());
    }

    public GameState nextState(String move) {
      return this.nextMoves.get(move);
    }
  }

  public interface GameState {
    boolean hasWon(String player);
    boolean isOver();
    List<String> nextMoves();
    GameState nextState(String move);
  }
}