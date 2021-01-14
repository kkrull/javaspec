package info.javaspec.jupiter.syntax.staticmethods;

import info.javaspec.jupiter.syntax.staticmethods.Minimax.GameState;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static info.javaspec.jupiter.syntax.staticmethods.JavaSpec.describe;
import static info.javaspec.jupiter.syntax.staticmethods.JavaSpec.it;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MinimaxWithStaticMethodSyntaxSpecs {
  @TestFactory DynamicNode makeSpecs() {
    return describe("Minimax", () -> {
      describe("#score", () -> {
        //Negative: Initialization like this runs at declaration time; bad for stateful objects.
        Minimax subject = new Minimax("Max", "Min");

        it("scores a game ending in a draw as 0", () -> {
          //Negative: Have to keep re-declaring the variable in each spec, instead of putting a field above
          GameWithKnownState game = new GameWithKnownState(true);
          assertEquals(0, subject.score(game, "Max"));
        });

        it("scores a game won by the maximizing player as +1", () -> {
          GameWithKnownState game = new GameWithKnownState(true, "Max");
          assertEquals(+1, subject.score(game, "Max"));
        });

        it("scores a game won by the minimizing player as -1", () -> {
          GameWithKnownState game = new GameWithKnownState(true, "Min");
          assertEquals(-1, subject.score(game, "Max"));
        });

        //Negative: No way to skip just one test, without commenting it out (which won't survive refactoring)
        it("scores the maximum possible score for the maximizing player, in an unfinished game", () -> {
          GameWithKnownState game = new GameWithKnownState(false);
          game.addKnownState("ThenDraw", new GameWithKnownState(true));
          game.addKnownState("ThenMaxWins", new GameWithKnownState(true, "Max"));
          assertEquals(+1, subject.score(game, "Max"));
        });
      });
    });
  }

  private static final class GameWithKnownState implements GameState {
    private final boolean _isOver;
    private final String winner;
    private final Map<String, GameWithKnownState> moveToGameState;

    public GameWithKnownState(boolean isOver) {
      this._isOver = isOver;
      this.winner = null;
      this.moveToGameState = new LinkedHashMap<>();
    }

    public GameWithKnownState(boolean isOver, String winner) {
      this._isOver = isOver;
      this.winner = winner;
      this.moveToGameState = new LinkedHashMap<>();
    }

    public void addKnownState(String nextMove, GameWithKnownState nextGame) {
      this.moveToGameState.put(nextMove, nextGame);
    }

    @Override
    public List<String> availableMoves() {
      return new ArrayList<>(this.moveToGameState.keySet());
    }

    @Override
    public String findWinner() {
      return this.winner;
    }

    @Override
    public boolean isOver() {
      return this._isOver;
    }

    @Override
    public GameState move(String nextMove) {
      return this.moveToGameState.get(nextMove);
    }
  }
}
