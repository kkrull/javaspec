package info.javaspec.jupiter.syntax.subject;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

import info.javaspec.jupiter.syntax.subject.Minimax.GameState;

@DisplayName("Subject syntax: Try Minimax")
class MinimaxWithSubjectSpecs {
	// Negative: Supporting data had to be moved up here, somewhat away from the
	// Minimax#score scope, to reach the helpers.
	String max = "Max";
	String min = "Min";

	@TestFactory
	DynamicNode makeSpecs() {
		JavaSpec<Minimax> javaspec = new JavaSpec<>();
		return javaspec.describe(Minimax.class, () -> {
			javaspec.subject(() -> new Minimax(max, min));

			// Negative: The description in the context does not show up in the gradle test
			// reporter I'm using
			javaspec.context("when the game is already over", () -> {
				javaspec.it("scores a game ending in a draw as 0", (subject) -> {
					GameWithKnownStates game = new GameWithKnownStates(true);
					assertEquals(0, subject.score(game, max));
				});

				javaspec.it("scores a game won by the maximizing player as +1", (subject) -> {
					GameWithKnownStates game = new GameWithKnownStates(true, max);
					assertEquals(+1, subject.score(game, max));
				});

				javaspec.it("scores a game won by the minimizing player as -1", (subject) -> {
					GameWithKnownStates game = new GameWithKnownStates(true, min);
					assertEquals(-1, subject.score(game, max));
				});
			});

			javaspec.context("when the next move will finish the game", () -> {
				javaspec.it("the maximizer picks the move with the highest score", (subject) -> {
					GameWithKnownStates game = new GameWithKnownStates(false);
					game.addKnownState("ThenDraw", new GameWithKnownStates(true));
					game.addKnownState("ThenMaxWins", new GameWithKnownStates(true, max));
					assertEquals(+1, subject.score(game, max));
				});

				javaspec.it("the minimizer picks the move with the lowest score", (subject) -> {
					GameWithKnownStates game = new GameWithKnownStates(false);
					game.addKnownState("ThenDraw", new GameWithKnownStates(true));
					game.addKnownState("ThenMaxLoses", new GameWithKnownStates(true, min));
					assertEquals(-1, subject.score(game, min));
				});
			});

			javaspec.context("when the game will end in 2 or more moves", () -> {
				javaspec.it("the maximizer assumes the minimizer will pick the lowest score", (subject) -> {
					GameWithKnownStates game = gameWithTwoMovesLeft();
					assertEquals(0, subject.score(game, max));
				});

				javaspec.it("the minimizer assumes the maximizer will pick the highest score", (subject) -> {
					// Negative: Repeating setup for non-subject entities (collaborators)
					GameWithKnownStates game = gameWithTwoMovesLeft();
					assertEquals(0, subject.score(game, min));
				});
			});
		});
	}

	private GameWithKnownStates gameWithTwoMovesLeft() {
		GameWithKnownStates game = new GameWithKnownStates(false);
		GameWithKnownStates leftTree = new GameWithKnownStates(false);
		game.addKnownState("Left", leftTree);
		leftTree.addKnownState("Draw", new GameWithKnownStates(true));
		leftTree.addKnownState("ThenMaxWins", new GameWithKnownStates(true, max));

		GameWithKnownStates rightTree = new GameWithKnownStates(false);
		game.addKnownState("Right", rightTree);
		rightTree.addKnownState("Draw", new GameWithKnownStates(true));
		rightTree.addKnownState("ThenMaxLoses", new GameWithKnownStates(true, min));
		return game;
	}

	private static final class GameWithKnownStates implements GameState {
		private final boolean isOver;
		private final String winner;
		private final Map<String, GameWithKnownStates> moveToGameState;

		public GameWithKnownStates(boolean isOver) {
			this.isOver = isOver;
			this.winner = null;
			this.moveToGameState = new LinkedHashMap<>();
		}

		public GameWithKnownStates(boolean isOver, String winner) {
			this.isOver = isOver;
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
			return this.isOver;
		}

		@Override
		public GameState move(String nextMove) {
			return this.moveToGameState.get(nextMove);
		}
	}
}
