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
