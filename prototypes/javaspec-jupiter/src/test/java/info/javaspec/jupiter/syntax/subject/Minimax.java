package info.javaspec.jupiter.syntax.subject;

final class Minimax {
  private final String maximizer;
  private final String minimizer;

  public Minimax(String maximizer, String minimizer) {
    this.maximizer = maximizer;
    this.minimizer = minimizer;
  }

  public int score(GameState game, String player) {
    if (game.isOver())
      return 0;

    return 999;
  }

  public interface GameState {
    boolean isOver();
  }
}
