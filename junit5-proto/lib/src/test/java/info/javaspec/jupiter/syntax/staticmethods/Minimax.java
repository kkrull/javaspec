package info.javaspec.jupiter.syntax.staticmethods;

final class Minimax {
  public int score(GameState game) {
    if(game.isOver())
      return 0;

    return 999;
  }

  interface GameState {
    boolean isOver();
  }
}
