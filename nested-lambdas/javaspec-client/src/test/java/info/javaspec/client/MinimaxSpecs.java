/**
 * MIT License
 *
 * Copyright (c) 2014–2022 Kyle Krull
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package info.javaspec.client;

import static org.junit.jupiter.api.Assertions.*;

import info.javaspec.api.JavaSpec;
import info.javaspec.api.SpecClass;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.platform.commons.annotation.Testable;

@Testable
public class MinimaxSpecs implements SpecClass {
	public void declareSpecs(JavaSpec javaspec) {
		javaspec.describe(Minimax.class, () -> {
			javaspec.it("scores a game ending in a draw as 0", () -> {
				Minimax subject = new Minimax("Max", "Min");
				GameState game = GameWithKnownState.draw();
				assertEquals(0, subject.score(game, "Max"));
			});

			javaspec.it("scores a game won by the maximizer as +1", () -> {
				Minimax subject = new Minimax("Max", "Min");
				GameState game = GameWithKnownState.wonBy("Max");
				assertEquals(+1, subject.score(game, "Max"));
			});

			javaspec.it("scores a game won by the minimizer as -1", () -> {
				Minimax subject = new Minimax("Max", "Min");
				GameState game = GameWithKnownState.wonBy("Min");
				assertEquals(-1, subject.score(game, "Max"));
			});

			javaspec.given("a game ending in 1 move", () -> {
				javaspec.it("the maximizer picks the move with the highest score", () -> {
					Minimax subject = new Minimax("Max", "Min");
					GameWithKnownState game = GameWithKnownState.stillGoing();
					game.addKnownState("ThenDraw", GameWithKnownState.draw());
					game.addKnownState("ThenMaxWins", GameWithKnownState.wonBy("Max"));
					assertEquals(+1, subject.score(game, "Max"));
				});

				javaspec.it("the minimizer picks the move with the lowest score", () -> {
					Minimax subject = new Minimax("Max", "Min");
					GameWithKnownState game = GameWithKnownState.stillGoing();
					game.addKnownState("ThenDraw", GameWithKnownState.draw());
					game.addKnownState("ThenMinWins", GameWithKnownState.wonBy("Min"));
					assertEquals(-1, subject.score(game, "Min"));
				});
			});

			javaspec.given("a game ending in 2 or more moves", () -> {
				javaspec.it("the maximizer assumes the minimizer picks the lowest score", () -> {
					Minimax subject = new Minimax("Max", "Min");
					GameState game = gameWithTwoMovesLeft();
					assertEquals(0, subject.score(game, "Max"));
				});

				javaspec.it("the minimizer assumes the maximizer picks the highest score", () -> {
					Minimax subject = new Minimax("Max", "Min");
					GameState game = gameWithTwoMovesLeft();
					assertEquals(0, subject.score(game, "Min"));
				});
			});
		});
	}

	private static GameState gameWithTwoMovesLeft() {
		GameWithKnownState game = GameWithKnownState.stillGoing();
		GameWithKnownState leftTree = GameWithKnownState.stillGoing();
		game.addKnownState("LeftTree", leftTree);
		leftTree.addKnownState("AndDraw", GameWithKnownState.draw());
		leftTree.addKnownState("AndMaxWins", GameWithKnownState.wonBy("Max"));

		GameWithKnownState rightTree = GameWithKnownState.stillGoing();
		game.addKnownState("RightTree", rightTree);
		rightTree.addKnownState("AndDraw", GameWithKnownState.draw());
		rightTree.addKnownState("AndMaxLoses", GameWithKnownState.wonBy("Min"));

		return game;
	}

	private static class GameWithKnownState implements GameState {
		private final boolean isOver;
		private final String winner;
		private final Map<String, GameState> nextMoves;

		public static GameWithKnownState draw() {
			return new GameWithKnownState(true, null);
		}

		public static GameWithKnownState stillGoing() {
			return new GameWithKnownState(false, null);
		}

		public static GameWithKnownState wonBy(String winner) {
			return new GameWithKnownState(true, winner);
		}

		private GameWithKnownState(boolean isOver, String winner) {
			this.isOver = isOver;
			this.winner = winner;
			this.nextMoves = new LinkedHashMap<String, GameState>();
		}

		public void addKnownState(String move, GameState state) {
			this.nextMoves.put(move, state);
		}

		public boolean hasWon(String player) {
			return player.equals(this.winner);
		}

		public boolean isOver() {
			return this.isOver;
		}

		public List<String> nextMoves() {
			return new ArrayList<>(this.nextMoves.keySet());
		}

		public GameState nextState(String move) {
			return this.nextMoves.get(move);
		}
	}
}
