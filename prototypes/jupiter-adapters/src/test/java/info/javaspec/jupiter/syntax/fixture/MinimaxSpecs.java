package info.javaspec.jupiter.syntax.fixture;

import static org.junit.jupiter.api.Assertions.assertEquals;

import info.javaspec.jupiter.syntax.fixture.Minimax.GameState;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

@DisplayName("Fixture syntax: Minimax")
class MinimaxSpecs {
	@TestFactory
	DynamicNode specs() {
		JavaSpec<Minimax> spec = new JavaSpec<>();
		return spec.describe(Minimax.class, () -> {
			spec.describe("score", () -> {
				String max = "Max";
				String min = "Min";
				spec.subject(() -> new Minimax(max, min));

				spec.context("given a game that is already over", () -> {
					spec.it("scores a game ending in a draw as 0", () -> {
						GameWithKnownStates game = new GameWithKnownStates(true);
						assertEquals(0, spec.subject().score(game, max));
					});

					spec.it("scores a game won by the maximizer as +1", () -> {
						GameWithKnownStates game = new GameWithKnownStates(true, max);
						assertEquals(+1, spec.subject().score(game, max));
					});

					spec.it("scores a game won by the minimizer as -1", () -> {
						GameWithKnownStates game = new GameWithKnownStates(true, min);
						assertEquals(-1, spec.subject().score(game, max));
					});
				});

				spec.context("given a game with 1 move left", () -> {
					spec.it("the maximizer picks the move with the highest score", () -> {
						GameWithKnownStates game = new GameWithKnownStates(false);
						game.addKnownState("ThenDraw", new GameWithKnownStates(true));
						game.addKnownState("ThenMaxWins", new GameWithKnownStates(true, max));
						assertEquals(+1, spec.subject().score(game, max));
					});

					spec.it("the minimizer picks the move with the lowest score", () -> {
						GameWithKnownStates game = new GameWithKnownStates(false);
						game.addKnownState("ThenDraw", new GameWithKnownStates(true));
						game.addKnownState("ThenMaxLoses", new GameWithKnownStates(true, min));
						assertEquals(-1, spec.subject().score(game, min));
					});
				});

				spec.context("given a game that has 2 or more moves left", () -> {
					AtomicReference<GameWithKnownStates> game = new AtomicReference<>();

					spec.beforeEach(() -> {
						GameWithKnownStates theGame = new GameWithKnownStates(false);
						GameWithKnownStates leftTree = new GameWithKnownStates(false);
						theGame.addKnownState("Left", leftTree);
						leftTree.addKnownState("ThenDraw", new GameWithKnownStates(true));
						leftTree.addKnownState("ThenMaxWins", new GameWithKnownStates(true, max));

						GameWithKnownStates rightTree = new GameWithKnownStates(false);
						theGame.addKnownState("Right", rightTree);
						rightTree.addKnownState("ThenDraw", new GameWithKnownStates(true));
						rightTree.addKnownState("ThenMaxLoses", new GameWithKnownStates(true, min));

						game.set(theGame);
					});

					spec.it("the maximizer assumes that the minimizer will pick the lowest score", () -> {
						assertEquals(0, spec.subject().score(game.get(), max));
					});

					spec.it("the minimizer assumes that the maximizer will pick the highest score", () -> {
						assertEquals(0, spec.subject().score(game.get(), min));
					});
				});
			});
		});
	}

	private static final class GameWithKnownStates implements GameState {
		private final boolean isOver;
		private final String winner;
		private final Map<String, GameWithKnownStates> nextGames = new LinkedHashMap<>();

		public GameWithKnownStates(boolean isOver) {
			this.isOver = isOver;
			this.winner = null;
		}

		public GameWithKnownStates(boolean isOver, String winner) {
			this.isOver = isOver;
			this.winner = winner;
		}

		public void addKnownState(String nextMove, GameWithKnownStates nextGame) {
			this.nextGames.put(nextMove, nextGame);
		}

		@Override
		public Collection<String> availableMoves() {
			return new ArrayList<>(this.nextGames.keySet());
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
		public GameState move(String move) {
			return this.nextGames.get(move);
		}
	}
}
