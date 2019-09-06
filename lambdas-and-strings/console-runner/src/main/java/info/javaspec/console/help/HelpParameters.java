package info.javaspec.console.help;

import com.beust.jcommander.Parameter;
import info.javaspec.console.Command;
import info.javaspec.console.CommandFactory;
import info.javaspec.console.MultiCommandParser;
import info.javaspec.console.Reporter;
import info.javaspec.console.ReporterFactory;

import java.util.Optional;

public final class HelpParameters implements MultiCommandParser.JCommanderParameters {
  private final CommandFactory commandFactory;
  private final ReporterFactory reporterFactory;

  @Parameter
  public String forCommandNamed;

  public HelpParameters(CommandFactory commandFactory, ReporterFactory reporterFactory) {
    this.commandFactory = commandFactory;
    this.reporterFactory = reporterFactory;
  }

  @Override
  public Command toExecutableCommand() {
    Reporter reporter = this.reporterFactory.plainTextReporter();
    return Optional.ofNullable(this.forCommandNamed)
      .map(helpOnWhat -> this.commandFactory.helpCommand(reporter, helpOnWhat))
      .orElseGet(() -> this.commandFactory.helpCommand(reporter));
  }
}
