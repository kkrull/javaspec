package info.javaspec.jupiter.syntax.subject;

final class Minimax {
  private final String maximizer;
  private final String minimizer;

  public Minimax(String maximizer, String minimizer) {
    this.maximizer = maximizer;
    this.minimizer = minimizer;
  }

  public int score(GameState game, String player) {
    if (player.equals(game.findWinner()))
      return +1;
    else if (game.isOver())
      return 0;

    return 999;
  }

  public interface GameState {
    String findWinner();
    boolean isOver();
  }
}
