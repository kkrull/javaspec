package info.javaspec.console.help;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.internal.Console;
import info.javaspec.console.Command;
import info.javaspec.console.Result;

import java.util.Arrays;
import java.util.Collections;

public final class HelpCommand implements Command {
  private final HelpObserver observer;

  public HelpCommand(HelpObserver observer) {
    this.observer = observer;
  }

  @Override
  public Result run() {
    this.observer.writeMessage(Arrays.asList(
      "Usage: javaspec <command> [<arguments>]",
      "",
      "## Commands ##",
      "",
      "help  show a list of commands, or help on a specific command",
      "run   run specs in Java classes"
    ));

    return Result.success();
  }

  public Result run(JCommander jCommander) {
    jCommander.setConsole(new Console() {
      @Override public void print(String msg) {
      }

      @Override public void println(String msg) {
        observer.writeMessage(Collections.singletonList(msg));
      }

      @Override public char[] readPassword(boolean echoInput) {
        return new char[0];
      }
    });

    jCommander.usage();
    return Result.success();
  }
}
