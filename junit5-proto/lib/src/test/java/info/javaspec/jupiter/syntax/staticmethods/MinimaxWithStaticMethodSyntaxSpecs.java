package info.javaspec.jupiter.syntax.staticmethods;

import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

import static info.javaspec.jupiter.syntax.staticmethods.JavaSpec.describe;
import static info.javaspec.jupiter.syntax.staticmethods.JavaSpec.it;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MinimaxWithStaticMethodSyntaxSpecs {
  @TestFactory DynamicNode makeSpecs() {
    return describe("Minimax", () -> {
      describe("#score", () -> {
        it("scores a game ending in a draw as 0", () -> {
          Minimax subject = new Minimax();
          Minimax.GameState game = new GameWithKnownState(true);
          assertEquals(0, subject.score(game));
        });
      });
    });
  }

  private static final class GameWithKnownState implements Minimax.GameState {
    private final boolean _isOver;

    public GameWithKnownState(boolean isOver) {
      this._isOver = isOver;
    }

    @Override
    public boolean isOver() {
      return this._isOver;
    }
  }
}
