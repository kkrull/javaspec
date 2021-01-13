package info.javaspec.jupiter.syntax.staticmethods;

final class Minimax {
  private final String maximizer;
  private final String minimizer;

  public Minimax(String maximizer, String minimizer) {
    this.maximizer = maximizer;
    this.minimizer = minimizer;
  }

  public int score(GameState game) {
    if(this.maximizer.equals(game.findWinner()))
      return +1;
    else if(this.minimizer.equals(game.findWinner()))
      return -1;
    else if(game.isOver())
      return 0;

    return 999;
  }

  interface GameState {
    String findWinner();
    boolean isOver();
  }
}
