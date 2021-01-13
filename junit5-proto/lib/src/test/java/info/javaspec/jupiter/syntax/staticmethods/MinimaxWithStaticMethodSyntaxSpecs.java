package info.javaspec.jupiter.syntax.staticmethods;

import info.javaspec.jupiter.syntax.staticmethods.Minimax.GameState;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

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
          GameState game = new GameWithKnownState(true);
          assertEquals(0, subject.score(game));
        });

        it("scores a game won by the maximizing player as +1", () -> {
          GameState game = new GameWithKnownState(true, "Max");
          assertEquals(+1, subject.score(game));
        });

        it("scores a game won by the minimizing player as -1", () -> {
          GameState game = new GameWithKnownState(true, "Min");
          assertEquals(-1, subject.score(game));
        });
      });
    });
  }

  private static final class GameWithKnownState implements GameState {
    private final boolean _isOver;
    private final String _winner;

    public GameWithKnownState(boolean isOver) {
      this._isOver = isOver;
      this._winner = null;
    }

    public GameWithKnownState(boolean isOver, String winner) {
      this._isOver = isOver;
      this._winner = winner;
    }

    @Override
    public String findWinner() {
      return this._winner;
    }

    @Override
    public boolean isOver() {
      return this._isOver;
    }
  }
}
