package info.javaspec.console.help;

import info.javaspec.console.Command;

import java.util.Arrays;

public final class DetailedHelpCommand implements Command {
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

  @Override
  public Result runResult() {
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

    return new Result(0);
  }
}
