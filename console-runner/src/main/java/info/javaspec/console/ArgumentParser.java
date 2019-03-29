package info.javaspec.console;

import info.javaspec.RunObserver;
import info.javaspec.console.HelpCommand.HelpObserver;

import java.util.List;

final class ArgumentParser implements Main.CommandParser {
  private final CommandFactory factory;
  private final Reporter reporter;

  public ArgumentParser(CommandFactory factory, Reporter reporter) {
    this.factory = factory;
    this.reporter = reporter;
  }

  @Override
  public Command parseCommand(List<String> args) {
    if(args.isEmpty())
      return this.factory.helpCommand(this.reporter);

    String command = args.get(0);
    switch(command) {
      case "help":
        return this.factory.helpCommand(this.reporter);

      case "run":
        return parseRunCommand(args);

      default:
        throw InvalidCommand.noCommandNamed(command);
    }
  }

  private Command parseRunCommand(List<String> args) {
    List<String> runArgs = args.subList(1, args.size());
    if(runArgs.isEmpty())
      throw InvalidCommand.noReporterDefined(args);
    else if(!"--reporter=plaintext".equals(runArgs.get(0)))
      throw InvalidCommand.noReporterDefined(args);

    List<String> classNames = runArgs.subList(1, runArgs.size());
    return this.factory.runSpecsCommand(this.reporter, classNames);
  }

  interface CommandFactory {
    Command helpCommand(HelpObserver observer);

    Command runSpecsCommand(RunObserver observer, List<String> classNames);
  }

  static final class InvalidCommand extends RuntimeException {
    public static InvalidCommand noCommandNamed(String command) {
      String message = String.format("Invalid command: %s", command);
      return new InvalidCommand(message);
    }

    public static InvalidCommand noReporterDefined(List<String> args) {
      String message = String.format("No reporter specified: %s", String.join(" ", args));
      return new InvalidCommand(message);
    }

    private InvalidCommand(String message) {
      super(message);
    }
  }
}
