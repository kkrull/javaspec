package info.javaspec.client;

import java.util.List;

interface Game {
	List<String> availableMoves();

	Player getWinner();

	boolean isOver();

	Game move(String nextMove);
}
