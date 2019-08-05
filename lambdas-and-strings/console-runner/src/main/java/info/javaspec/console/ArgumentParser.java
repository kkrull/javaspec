package info.javaspec.console;

import com.beust.jcommander.JCommander;
import info.javaspec.RunObserver;
import info.javaspec.console.help.HelpArguments;
import info.javaspec.console.help.HelpObserver;

import java.util.List;

final class ArgumentParser implements Main.CommandParser {
  private final CommandFactory factory;
  private final Reporter reporter;

  public ArgumentParser(CommandFactory factory, Reporter reporter) {
    this.factory = factory;
    this.reporter = reporter;
  }

  @Override
  public Command parseCommand(List<String> commandThenArguments) {
    if(commandThenArguments.isEmpty())
      return this.factory.helpCommand(this.reporter);

    String command = commandThenArguments.get(0);
    List<String> arguments = commandThenArguments.subList(1, commandThenArguments.size());
    switch(command) {
      case "help":
        return parseHelpCommand(arguments);

      case "run":
        return parseRunCommand(commandThenArguments, arguments);

      default:
        throw InvalidCommand.noCommandNamed(command);
    }
  }

  private Command parseHelpCommand(List<String> stringArguments) {
    HelpArguments helpArguments = new HelpArguments();
    JCommander.newBuilder()
      .addObject(helpArguments)
      .build()
      .parse(stringArguments.toArray(new String[0]));

    if(helpArguments.hasCommandParameter())
      return this.factory.helpCommand(this.reporter, helpArguments.forCommandNamed);

    return this.factory.helpCommand(this.reporter);
  }

  private Command parseRunCommand(List<String> commandThenArguments, List<String> arguments) {
    if(arguments.isEmpty())
      throw InvalidCommand.noReporterDefined(commandThenArguments);
    else if(!"--reporter=plaintext".equals(arguments.get(0)))
      throw InvalidCommand.noReporterDefined(commandThenArguments);

    List<String> classNames = arguments.subList(1, arguments.size());
    return this.factory.runSpecsCommand(this.reporter, classNames);
  }

  interface CommandFactory {
    Command helpCommand(HelpObserver observer);

    Command helpCommand(HelpObserver observer, String forCommandNamed);

    Command runSpecsCommand(RunObserver observer, List<String> classNames);
  }

  static final class InvalidCommand extends RuntimeException {
    public static InvalidCommand noCommandNamed(String command) {
      String message = String.format("Invalid command: %s", command);
      return new InvalidCommand(message);
    }

    public static InvalidCommand noReporterDefined(List<String> args) {
      String message = String.format(
        "%s: No reporter specified.  Please use the --reporter option.",
        String.join(" ", args));
      return new InvalidCommand(message);
    }

    private InvalidCommand(String message) {
      super(message);
    }
  }
}
