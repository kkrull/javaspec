package info.javaspec.jupiter.syntax.staticmethods;

import info.javaspec.jupiter.syntax.staticmethods.Minimax.GameState;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static info.javaspec.jupiter.syntax.staticmethods.JavaSpec.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MinimaxWithStaticMethodSyntaxSpecs {
  @TestFactory DynamicNode makeSpecs() {
    //Positive: Works, while being much more concise than plain JUnit.
    return describe("Minimax", () -> {
      describe("#score", () -> {
        //Negative: Initialization like this runs at declaration time; bad for stateful objects.
        final String max = "Max";
        final String min = "Min";
        final Minimax subject = new Minimax(max, min);

        context("when the game is already over", () -> {
          it("scores a game ending in a draw as 0", () -> {
            //Negative: Have to keep re-declaring the variable in each spec, instead of putting a field above
            GameWithKnownStates game = new GameWithKnownStates(true);
            assertEquals(0, subject.score(game, max));
          });

          it("scores a game won by the maximizing player as +1", () -> {
            GameWithKnownStates game = new GameWithKnownStates(true, max);
            assertEquals(+1, subject.score(game, max));
          });

          it("scores a game won by the minimizing player as -1", () -> {
            GameWithKnownStates game = new GameWithKnownStates(true, min);
            assertEquals(-1, subject.score(game, max));
          });
        });

        context("when the game is not over yet", () -> {
          //Negative: No way to skip just one test, without commenting it out (which won't survive refactoring)
          it("the maximizer picks the move with the highest score", () -> {
            GameWithKnownStates game = new GameWithKnownStates(false);
            game.addKnownState("ThenDraw", new GameWithKnownStates(true));
            game.addKnownState("ThenMaxWins", new GameWithKnownStates(true, max));
            assertEquals(+1, subject.score(game, max));
          });

          it("the minimizer picks the move with the lowest score", () -> {
            GameWithKnownStates game = new GameWithKnownStates(false);
            game.addKnownState("ThenDraw", new GameWithKnownStates(true));
            game.addKnownState("ThenMaxLoses", new GameWithKnownStates(true, min));
            assertEquals(-1, subject.score(game, min));
          });
        });

        context("when the game has 2 or more moves left", () -> {
          it("the maximizer assumes the minimizer picks the lowest score", () -> {
            GameWithKnownStates game = gameWithTwoMovesLeft();
            assertEquals(0, subject.score(game, max));
          });

          it("the minimizer assumes the maximizer picks the highest score", () -> {
            GameWithKnownStates game = gameWithTwoMovesLeft();
            assertEquals(0, subject.score(game, min));
          });
        });
      });
    });
  }

  private static GameWithKnownStates gameWithTwoMovesLeft() {
    GameWithKnownStates game = new GameWithKnownStates(false);
    GameWithKnownStates leftTree = new GameWithKnownStates(false);
    game.addKnownState("Left", leftTree);
    leftTree.addKnownState("Draw", new GameWithKnownStates(true));
    leftTree.addKnownState("ThenMaxWins", new GameWithKnownStates(true, "Max"));

    GameWithKnownStates rightTree = new GameWithKnownStates(false);
    game.addKnownState("Right", rightTree);
    rightTree.addKnownState("Draw", new GameWithKnownStates(true));
    rightTree.addKnownState("ThenMaxLoses", new GameWithKnownStates(true, "Min"));

    return game;
  }

  private static final class GameWithKnownStates implements GameState {
    private final boolean _isOver;
    private final String winner;
    private final Map<String, GameWithKnownStates> moveToGameState;

    public GameWithKnownStates(boolean isOver) {
      this._isOver = isOver;
      this.winner = null;
      this.moveToGameState = new LinkedHashMap<>();
    }

    public GameWithKnownStates(boolean isOver, String winner) {
      this._isOver = isOver;
      this.winner = winner;
      this.moveToGameState = new LinkedHashMap<>();
    }

    public void addKnownState(String nextMove, GameWithKnownStates nextGame) {
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
