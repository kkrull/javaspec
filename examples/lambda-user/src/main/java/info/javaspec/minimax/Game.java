package info.javaspec.example.minimax;

import java.util.List;

interface Game {
	List<String> availableMoves();

	Player getWinner();

	boolean isOver();

	Game move(String nextMove);
}
