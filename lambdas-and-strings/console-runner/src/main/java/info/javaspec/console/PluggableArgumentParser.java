package info.javaspec.console;

import java.util.List;

public class PluggableArgumentParser implements Main.ArgumentParser {
  private final CommandParser mainParser;

  public PluggableArgumentParser(CommandParser mainParser) {
    this.mainParser = mainParser;
  }

  @Override
  public Command parseCommand(List<String> arguments) {
    return this.mainParser.parseCommand(arguments);
  }

  @FunctionalInterface
  public interface CommandParser {
    Command parseCommand(List<String> commandArguments);
  }
}
