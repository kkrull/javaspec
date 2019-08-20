package info.javaspec.console;

import com.beust.jcommander.JCommander;
import info.javaspec.console.help.HelpArguments;
import info.javaspec.lang.lambda.RunArguments;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

final class ArgumentParser implements Main.CommandParser {
  private final CommandFactory commandFactory;
  private final ReporterFactory reporterFactory;

  public ArgumentParser(CommandFactory commandFactory, ReporterFactory reporterFactory) {
    this.commandFactory = commandFactory;
    this.reporterFactory = reporterFactory;
  }

  @Override
  public Command parseCommand(List<String> commandThenArguments) {
    Reporter reporter = this.reporterFactory.plainTextReporter();
    if(commandThenArguments.isEmpty())
      return this.commandFactory.helpCommand(reporter);

    String command = commandThenArguments.get(0);
    List<String> arguments = commandThenArguments.subList(1, commandThenArguments.size());
    Optional<String> helpOnWhat = parseHelpOptionOnAnotherCommand(command, arguments);

    if(helpOnWhat.isPresent()) {
      return parseHelpCommand(Collections.singletonList(helpOnWhat.get()));
    }

    switch(command) {
      case "help":
        return parseHelpCommand(arguments);

      case "run":
        return parseRunCommand(arguments);

      default:
        throw InvalidCommand.noCommandNamed(command);
    }
  }

  //TODO KDK: Maybe better to have a method that parses the total list into a command and a sub-list of arguments?
  private Optional<String> parseHelpOptionOnAnotherCommand(String command, List<String> arguments) {
    return arguments.stream()
        .filter("--help"::equals)
        .map(_x -> command)
        .findFirst();
  }

  private Command parseHelpCommand(List<String> stringArguments) {
    HelpArguments helpArguments = new HelpArguments(this.commandFactory, this.reporterFactory);
    JCommander.newBuilder()
      .addObject(helpArguments)
      .build()
      .parse(stringArguments.toArray(new String[0]));

    Reporter reporter = this.reporterFactory.plainTextReporter();
    if(helpArguments.hasCommandParameter())
      return this.commandFactory.helpCommand(reporter, helpArguments.forCommandNamed);

    return this.commandFactory.helpCommand(reporter);
  }

  private Command parseRunCommand(List<String> stringArguments) {
    RunArguments runArguments = new RunArguments();
    JCommander.newBuilder()
      .addObject(runArguments)
      .build()
      .parse(stringArguments.toArray(new String[0]));

    return this.commandFactory.runSpecsCommand(
      this.reporterFactory.plainTextReporter(),
      runArguments.specClassPath(),
      runArguments.specClassNames()
    );
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
