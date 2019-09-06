package info.javaspec.console;

import info.javaspec.console.help.HelpArguments;
import info.javaspec.lang.lambda.RunArguments;

import java.util.List;

final class StaticArgumentParser implements Main.ArgumentParser {
  private final CommandFactory commandFactory;
  private final ReporterFactory reporterFactory;

  public StaticArgumentParser(CommandFactory commandFactory, ReporterFactory reporterFactory) {
    this.commandFactory = commandFactory;
    this.reporterFactory = reporterFactory;
  }

  @Override
  public Command parseCommand(List<String> commandThenArguments) {
    MainParameters mainParameters = new MainParameters(this.commandFactory, this.reporterFactory);
    return mainParameters.parseCommand(commandThenArguments)
      .orElseGet(() -> parseSubCommand(commandThenArguments));
  }

  private Command parseSubCommand(List<String> commandThenArguments) {
    String command = commandThenArguments.get(0);
    List<String> arguments = commandThenArguments.subList(1, commandThenArguments.size());

    switch(command) {
      case "help":
        HelpArguments helpArguments = new HelpArguments(this.commandFactory, this.reporterFactory);
        return helpArguments.parseCommand(arguments);

      case "run":
        RunArguments runArguments = new RunArguments(this.commandFactory, this.reporterFactory);
        return runArguments.parseCommand(arguments);

      default:
        throw InvalidCommand.noCommandNamed(command);
    }
  }

  static final class InvalidCommand extends RuntimeException {
    public static InvalidCommand noCommandNamed(String command) {
      String message = String.format("Invalid command: %s", command);
      return new InvalidCommand(message);
    }

    private InvalidCommand(String message) {
      super(message);
    }
  }
}
