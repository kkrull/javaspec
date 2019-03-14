package info.javaspec.console;

import java.util.Arrays;
import java.util.List;

final class HelpCommand implements Command {
  private final HelpObserver observer;

  public HelpCommand(HelpObserver observer) {
    this.observer = observer;
  }

  @Override
  public int run() {
    this.observer.writeMessage(Arrays.asList(
      "Usage: javaspec <command> [<arguments>]",
      "",
      "Commands:",
      "  help",
      "    show this help",
      "",
      "  run <spec class name> [spec class name...]",
      "    run specs in Java classes"
    ));

    return 0;
  }

  public interface HelpObserver {
    void writeMessage(List<String> lines);
  }
}
