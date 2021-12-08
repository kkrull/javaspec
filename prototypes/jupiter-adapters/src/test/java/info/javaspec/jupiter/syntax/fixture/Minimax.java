package info.javaspec.jupiter.syntax.fixture;

import java.util.Collection;

final class Minimax {
  private final String maximizerPlayer;
  private final String minimizerPlayer;

  public Minimax(String maximizerPlayer, String minimizerPlayer) {
    this.maximizerPlayer = maximizerPlayer;
    this.minimizerPlayer = minimizerPlayer;
  }

  public int score(GameState game, String player) {
    if(this.maximizerPlayer.equals(game.findWinner())) {
      return +1;
    } else if(this.minimizerPlayer.equals(game.findWinner())) {
      return -1;
    } else if(game.isOver()) {
      return 0;
    }

    if(this.maximizerPlayer.equals(player)) {
      int bestScore = -100;
      for(String nextMove : game.availableMoves()) {
        GameState nextGame = game.move(nextMove);
        int nextScore = score(nextGame, this.minimizerPlayer);
        if(nextScore > bestScore) {
          bestScore = nextScore;
        }
      }

      return bestScore;
    } else if(this.minimizerPlayer.equals(player)) {
      int bestScore = +100;
      for(String nextMove : game.availableMoves()) {
        GameState nextGame = game.move(nextMove);
        int nextScore = score(nextGame, this.maximizerPlayer);
        if(nextScore < bestScore) {
          bestScore = nextScore;
        }
      }

      return bestScore;
    }

    return 9999;
  }

  interface GameState {
    Collection<String> availableMoves();
    String findWinner();
    boolean isOver();
    GameState move(String move);
  }
}
