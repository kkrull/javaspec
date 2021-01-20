package info.javaspec.jupiter.syntax.subject;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

import info.javaspec.jupiter.syntax.subject.Minimax.GameState;

@DisplayName("Subject syntax: Try Minimax")
class MinimaxWithSubjectSpecs {
  @TestFactory DynamicNode makeSpecs() {
    JavaSpec<Minimax> javaspec = new JavaSpec<>();
    return javaspec.describe(Minimax.class, () -> {
      String max = "Max";
      String min = "Min";
      javaspec.subject(() -> new Minimax(max, min));

      //Negative: The description in the context does not show up in the gradle test reporter I'm using
      javaspec.context("when the game is already over", () -> {
        javaspec.it("scores a game ending in a draw as 0", () -> {
          GameWithKnownStates game = new GameWithKnownStates(true);
          assertEquals(0, javaspec.subject().score(game, max));
        });

        javaspec.it("scores a game won by the maximizing player as +1", () -> {
          GameWithKnownStates game = new GameWithKnownStates(true, max);
          assertEquals(+1, javaspec.subject().score(game, max));
        });

        javaspec.it("scores a game won by the minmizing player as -1", () -> {
          GameWithKnownStates game = new GameWithKnownStates(true, min);
          assertEquals(-1, javaspec.subject().score(game, max));
        });
      });
    });
  }

  private static final class GameWithKnownStates implements GameState {
    private final boolean isOver;
    private final String winner;

    public GameWithKnownStates(boolean isOver) {
      this.isOver = isOver;
      this.winner = null;
    }

    public GameWithKnownStates(boolean isOver, String winner) {
      this.isOver = isOver;
      this.winner = winner;
    }

    @Override
    public String findWinner() {
      return this.winner;
    }

    @Override
    public boolean isOver() {
      return this.isOver;
    }
  }
}
