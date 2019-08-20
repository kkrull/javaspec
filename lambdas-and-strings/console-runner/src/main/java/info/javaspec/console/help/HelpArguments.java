package info.javaspec.console.help;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import info.javaspec.console.Command;
import info.javaspec.console.CommandFactory;
import info.javaspec.console.Reporter;
import info.javaspec.console.ReporterFactory;

import java.util.List;
import java.util.Optional;

public final class HelpArguments {
  private final CommandFactory commandFactory;
  private final ReporterFactory reporterFactory;

  @Parameter()
  public String forCommandNamed;

  public HelpArguments(CommandFactory commandFactory, ReporterFactory reporterFactory) {
    this.commandFactory = commandFactory;
    this.reporterFactory = reporterFactory;
  }

  public Command parseCommand(List<String> stringArguments) {
    JCommander.newBuilder()
      .addObject(this)
      .build()
      .parse(stringArguments.toArray(new String[0]));

    Reporter reporter = this.reporterFactory.plainTextReporter();
    return Optional.ofNullable(this.forCommandNamed)
      .map(helpOnWhat -> this.commandFactory.helpCommand(reporter, helpOnWhat))
      .orElseGet(() -> this.commandFactory.helpCommand(reporter));
  }
}
