package info.javaspec.client;

class Minimax {
	private final Player maximizer;
	private final Player minimizer;

	public Minimax(Player maximizer, Player minimizer) {
		this.maximizer = maximizer;
		this.minimizer = minimizer;
	}

	public int score(Game game, Player player) {
		if (game.getWinner() == this.maximizer) {
			return +1;
		} else if (game.getWinner() == this.minimizer) {
			return -1;
		} else if (game.isOver()) {
			return 0;
		}

		if (player == this.maximizer) {
			int highestPossibleScore = -999;
			for (String nextMove : game.availableMoves()) {
				Game nextGameState = game.move(nextMove);
				int nextScore = score(nextGameState, this.minimizer);
				if (nextScore > highestPossibleScore) {
					highestPossibleScore = nextScore;
				}
			}

			return highestPossibleScore;
		} else {
			int lowestPossibleScore = +999;
			for (String nextMove : game.availableMoves()) {
				Game nextGameState = game.move(nextMove);
				int nextScore = score(nextGameState, this.maximizer);
				if (nextScore < lowestPossibleScore) {
					lowestPossibleScore = nextScore;
				}
			}

			return lowestPossibleScore;
		}
	}
}
