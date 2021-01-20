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
      javaspec.it("scores a game ending in a draw as 0", () -> {
        String max = "Max";
        String min = "Min";
        Minimax subject = new Minimax(max, min);

        GameWithKnownStates game = new GameWithKnownStates(true);
        assertEquals(0, subject.score(game, max));
      });
    });
  }

  private static final class GameWithKnownStates implements GameState {
    private final boolean isOver;

    public GameWithKnownStates(boolean isOver) {
      this.isOver = isOver;
    }

    @Override
    public boolean isOver() {
      return this.isOver;
    }
  }
}
