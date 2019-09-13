package info.javaspec.console.help;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.internal.Console;
import info.javaspec.console.Command;
import info.javaspec.console.Result;

import java.util.Collections;

public final class HelpCommand implements Command {
  private final HelpObserver observer;
  private JCommander jCommander;

  public HelpCommand(HelpObserver observer, JCommander jCommander) {
    this.observer = observer;
    this.jCommander = jCommander;
  }

  @Override
  public Result run() {
    Console originalConsole = this.jCommander.getConsole();
    this.jCommander.setConsole(new ConsoleToHelpObserverAdapter(observer));
    this.jCommander.usage();
    this.jCommander.setConsole(originalConsole);
    return Result.success();
  }

  private static final class ConsoleToHelpObserverAdapter implements Console {
    private final HelpObserver observer;

    public ConsoleToHelpObserverAdapter(HelpObserver observer) {
      this.observer = observer;
    }

    @Override
    public void print(String msg) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void println(String msg) {
      this.observer.writeMessage(Collections.singletonList(msg));
    }

    @Override
    public char[] readPassword(boolean echoInput) {
      throw new UnsupportedOperationException("This Console is only used to print usage");
    }
  }
}
