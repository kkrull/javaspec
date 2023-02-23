package info.javaspec.client;

import info.javaspec.api.JavaSpec;
import info.javaspec.api.SpecClass;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.platform.commons.annotation.Testable;

@Testable
public class MinimaxSpecs implements SpecClass {
	private final Player playerMax = new Player();
	private final Player playerMin = new Player();

	@Override
	public void declareSpecs(JavaSpec javaspec) {
		javaspec.describe(Minimax.class, () -> {
			Minimax subject = new Minimax(playerMax, playerMin);

			javaspec.describe("#score", () -> {
				javaspec.given("a game that is already over", () -> {
					javaspec.it("returns 0 for a game ending in a draw", () -> {
						GameStub game = GameStub.drawGame();
						assertEquals(0, subject.score(game, playerMax));
					});

					javaspec.it("returns +1 for a game won by the maximizer", () -> {
						GameStub game = GameStub.wonBy(playerMax);
						assertEquals(+1, subject.score(game, playerMax));
					});

					javaspec.it("returns -1 for a game won by the minimizer", () -> {
						GameStub game = GameStub.wonBy(playerMin);
						assertEquals(-1, subject.score(game, playerMax));
					});
				});

				javaspec.given("a game that will be over in 1 more turn", () -> {
					javaspec.it("the maximizer picks the move with the highest score", () -> {
						GameStub game = GameStub.onGoing();
						game.addKnownState("ThenDraw", GameStub.drawGame());
						game.addKnownState("ThenMaxWins", GameStub.wonBy(playerMax));
						assertEquals(+1, subject.score(game, playerMax));
					});

					javaspec.it("the minimizer picks the move with the lowest score", () -> {
						GameStub game = GameStub.onGoing();
						game.addKnownState("ThenDraw", GameStub.drawGame());
						game.addKnownState("ThenMaxLoses", GameStub.wonBy(playerMin));
						assertEquals(-1, subject.score(game, playerMin));
					});
				});

				javaspec.given("a game that will be over in 2 or more turns", () -> {
					javaspec.it("the maximizer assumes the minimizer will pick the lowest score", () -> {
						GameStub game = gameWithTwoTurnsLeft();
						assertEquals(0, subject.score(game, playerMax));
					});

					javaspec.it("the minimizer assumes the maximizer will pick the highest score", () -> {
						GameStub game = gameWithTwoTurnsLeft();
						assertEquals(0, subject.score(game, playerMin));
					});
				});
			});
		});
	}

	private GameStub gameWithTwoTurnsLeft() {
		GameStub leftTree = GameStub.onGoing();
		leftTree.addKnownState("Draw", GameStub.drawGame());
		leftTree.addKnownState("ThenMaxWins", GameStub.wonBy(this.playerMax));

		GameStub rightTree = GameStub.onGoing();
		rightTree.addKnownState("Draw", GameStub.drawGame());
		rightTree.addKnownState("ThenMaxLoses", GameStub.wonBy(this.playerMin));

		GameStub game = GameStub.onGoing();
		game.addKnownState("Left", leftTree);
		game.addKnownState("Right", rightTree);
		return game;
	}

	private static class GameStub implements Game {
		private final boolean isOver;
		private final Player winner;
		private final Map<String, GameStub> nextMoves;

		public static GameStub drawGame() {
			return new GameStub(true, null);
		}

		public static GameStub onGoing() {
			return new GameStub(false, null);
		}

		public static GameStub wonBy(Player player) {
			return new GameStub(true, player);
		}

		private GameStub(boolean isOver, Player winner) {
			this.isOver = isOver;
			this.winner = winner;
			this.nextMoves = new LinkedHashMap<String, GameStub>();
		}

		public void addKnownState(String nextMove, GameStub nextState) {
			this.nextMoves.put(nextMove, nextState);
		}

		@Override
		public List<String> availableMoves() {
			return new ArrayList<>(this.nextMoves.keySet());
		}

		@Override
		public Player getWinner() {
			return this.winner;
		}

		@Override
		public boolean isOver() {
			return this.isOver;
		}

		@Override
		public Game move(String nextMove) {
			return this.nextMoves.get(nextMove);
		}
	}
}
