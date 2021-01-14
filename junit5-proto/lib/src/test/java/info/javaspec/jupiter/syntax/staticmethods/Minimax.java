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

    if(this.maximizer.equals(player)) {
      int maxScore = -100;
      for(String nextMove : game.availableMoves()) {
        GameState nextGame = game.move(nextMove);
        int nextScore = score(nextGame, this.minimizer);
        if(nextScore > maxScore) {
          maxScore = nextScore;
        }
      }

      return maxScore;
    } else if(this.minimizer.equals(player)) {
      int minScore = +100;
      for(String nextMove : game.availableMoves()) {
        GameState nextGame = game.move(nextMove);
        int nextScore = score(nextGame, this.maximizer);
        if(nextScore < minScore) {
          minScore = nextScore;
        }
      }

      return minScore;
    } else {
      return 999;
    }
  }

  interface GameState {
    List<String> availableMoves();
    String findWinner();
    boolean isOver();
    GameState move(String move);
  }
}
