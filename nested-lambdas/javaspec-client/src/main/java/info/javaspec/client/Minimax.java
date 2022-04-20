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

public class Minimax {
	private final String maximizer;
	private final String minimizer;

	public Minimax(String maximizer, String minimizer) {
		this.maximizer = maximizer;
		this.minimizer = minimizer;
	}

	public int score(GameState game, String player) {
		if (game.hasWon(this.maximizer)) {
			return +1;
		} else if (game.hasWon(this.minimizer)) {
			return -1;
		} else if (game.isOver()) {
			return 0;
		}

		if (this.minimizer.equals(player)) {
			int minScore = +999;
			for (String nextMove : game.nextMoves()) {
				GameState nextGame = game.nextState(nextMove);
				int score = score(nextGame, this.maximizer);
				if (score < minScore) {
					minScore = score;
				}
			}

			return minScore;
		} else {
			int maxScore = -999;
			for (String nextMove : game.nextMoves()) {
				GameState nextGame = game.nextState(nextMove);
				int score = score(nextGame, this.minimizer);
				if (score > maxScore) {
					maxScore = score;
				}
			}

			return maxScore;
		}
	}
}
