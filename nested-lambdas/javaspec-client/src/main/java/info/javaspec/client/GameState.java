package info.javaspec.client;

import java.util.List;

public interface GameState {
	boolean hasWon(String player);
	boolean isOver();
	List<String> nextMoves();
	GameState nextState(String move);
}
