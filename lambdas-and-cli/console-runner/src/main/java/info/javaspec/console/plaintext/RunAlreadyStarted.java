package info.javaspec.console.plaintext;

final class RunAlreadyStarted extends IllegalStateException {
  public RunAlreadyStarted() {
    super("Tried to start a new run, before finishing the current one."
      + "  Please call #runFinished, first."
    );
  }
}
