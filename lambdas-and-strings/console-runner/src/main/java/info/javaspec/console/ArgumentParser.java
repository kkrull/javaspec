package info.javaspec.console;

import com.beust.jcommander.JCommander;
import info.javaspec.RunObserver;
import info.javaspec.console.help.HelpArguments;
import info.javaspec.console.help.HelpObserver;
import info.javaspec.lang.lambda.RunArguments;

import java.util.List;

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
    switch(command) {
      case "help":
        return parseHelpCommand(arguments);

      case "run":
        return parseRunCommand(arguments);

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

  interface CommandFactory {
    Command helpCommand(HelpObserver observer);

    Command helpCommand(HelpObserver observer, String forCommandNamed);

    Command runSpecsCommand(RunObserver observer, String specClassPath, List<String> classNames);
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
