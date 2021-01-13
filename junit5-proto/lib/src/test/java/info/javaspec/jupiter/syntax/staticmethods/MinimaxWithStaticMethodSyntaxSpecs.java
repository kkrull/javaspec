package info.javaspec.jupiter.syntax.staticmethods;

import info.javaspec.jupiter.syntax.staticmethods.Minimax.GameState;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

import java.util.LinkedList;
import java.util.List;

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
          assertEquals(0, subject.score(game));
        });

        it("scores a game won by the maximizing player as +1", () -> {
          GameWithKnownState game = new GameWithKnownState(true, "Max");
          assertEquals(+1, subject.score(game));
        });

        it("scores a game won by the minimizing player as -1", () -> {
          GameWithKnownState game = new GameWithKnownState(true, "Min");
          assertEquals(-1, subject.score(game));
        });

        //Negative: No way to skip just one test, without commenting it out (which won't survive refactoring)
        it("scores the maximum possible score for the maximizing player, in an unfinished game", () -> {
          GameWithKnownState game = new GameWithKnownState(false);
          game.addKnownState("ThenDraw", new GameWithKnownState(true));
          game.addKnownState("ThenMaxWins", new GameWithKnownState(true, "Max"));
          assertEquals(+1, subject.score(game));
        });
      });
    });
  }

  private static final class GameWithKnownState implements GameState {
    private final boolean _isOver;
    private final String winner;
    private final List<String> moves;
    private final List<GameWithKnownState> games;

    public GameWithKnownState(boolean isOver) {
      this._isOver = isOver;
      this.winner = null;
      this.moves = new LinkedList<>();
      this.games = new LinkedList<>();
    }

    public GameWithKnownState(boolean isOver, String winner) {
      this._isOver = isOver;
      this.winner = winner;
      this.moves = new LinkedList<>();
      this.games = new LinkedList<>();
    }

    public void addKnownState(String nextMove, GameWithKnownState nextGame) {
      this.moves.add(nextMove);
      this.games.add(nextGame);
    }

    @Override
    public String findWinner() {
      return this.winner;
    }

    @Override
    public boolean isOver() {
      return this._isOver;
    }
  }
}
