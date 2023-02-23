package info.javaspec.client;

class Minimax {
	private final Player maximizer;
	private final Player minimizer;

	public Minimax(Player maximizer, Player minimizer) {
		this.maximizer = maximizer;
		this.minimizer = minimizer;
	}

	public int score(Game game, Player player) {
		if (player == this.maximizer) {
			return +1 * negamaxScore(game, +1);
		} else {
			return -1 * negamaxScore(game, -1);
		}
	}

	private int negamaxScore(Game game, int polarity) {
		if (game.getWinner() == this.maximizer) {
			return polarity;
		} else if (game.getWinner() == this.minimizer) {
			return -1 * polarity;
		} else if (game.isOver()) {
			return 0;
		}

		int highestPossibleScore = -999;
		for (String nextMove : game.availableMoves()) {
			Game nextGameState = game.move(nextMove);
			int nextScore = -1 * negamaxScore(nextGameState, -1 * polarity);
			if (nextScore > highestPossibleScore) {
				highestPossibleScore = nextScore;
			}
		}

		return highestPossibleScore;
	}
}
