package info.javaspec.jupiter.syntax.staticmethods;

import java.util.List;

final class Minimax {
	private final String maximizer;
	private final String minimizer;

	public Minimax(String maximizer, String minimizer) {
		this.maximizer = maximizer;
		this.minimizer = minimizer;
	}

	public int score(GameState game, String player) {
		if (this.maximizer.equals(player))
			return negamax(game, 1);
		else
			return -negamax(game, -1);
	}

	private int negamax(GameState game, int polarity) {
		if (this.maximizer.equals(game.findWinner()))
			return polarity;
		else if (this.minimizer.equals(game.findWinner()))
			return -1 * polarity;
		else if (game.isOver())
			return 0;

		int maxScore = -100;
		for (String nextMove : game.availableMoves()) {
			GameState nextGame = game.move(nextMove);
			int nextScore = -1 * negamax(nextGame, -1 * polarity);
			if (nextScore > maxScore) {
				maxScore = nextScore;
			}
		}

		return maxScore;
	}

	interface GameState {
		List<String> availableMoves();
		String findWinner();
		boolean isOver();
		GameState move(String move);
	}
}
