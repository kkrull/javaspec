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
    if(this.maximizer.equals(game.findWinner()))
      return +1;
    else if(this.minimizer.equals(game.findWinner()))
      return -1;
    else if(game.isOver())
      return 0;

    int bestScore = -100;
    for(String nextMove : game.availableMoves()) {
      GameState nextGame = game.move(nextMove);
      int nextScore = score(nextGame, this.minimizer);
      if(nextScore > bestScore) {
        bestScore = nextScore;
      }
    }

    return bestScore;
  }

  interface GameState {
    List<String> availableMoves();
    String findWinner();
    boolean isOver();
    GameState move(String move);
  }
}
