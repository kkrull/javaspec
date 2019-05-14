package info.javaspec.console;

import java.util.Arrays;

final class DetailedHelpCommand implements Command {
  private final HelpObserver observer;
  private final String forCommandNamed;

  public DetailedHelpCommand(HelpObserver observer, String forCommandNamed) {
    this.observer = observer;
    this.forCommandNamed = forCommandNamed;
  }

  @Override
  public int run() {
    this.observer.writeMessage(Arrays.asList(
      "Usage:   javaspec run --reporter=plaintext <spec class> [spec class...]",
      "Example: javaspec run --reporter=plaintext com.acme.AnvilSpecs com.acme.SpringOperatedBoxingGloveSpecs",
      "",
      "## Options ##",
      "",
      "--reporter=[reporter]   How you want to find out which spec is running and what their results are",
      "",
      "  plaintext   Plain-text output without any colors or other escape sequences.",
      "              Useful for preventing garbled output on continuous integration servers."
    ));

    return 0;
  }
}
