package info.javaspec.console;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

public final class MainParameters implements MultiCommandParser.JCommanderParameters {
  private final CommandFactory commandFactory;
  private final ReporterFactory reporterFactory;

  @Parameter(
    description = "Show help",
    help = true,
    names = "--help"
  )
  private boolean isAskingForHelp;

  public MainParameters(CommandFactory commandFactory, ReporterFactory reporterFactory) {
    this.commandFactory = commandFactory;
    this.reporterFactory = reporterFactory;
  }

  @Override
  public Command toExecutableCommand(JCommander parser) {
    Reporter reporter = this.reporterFactory.plainTextReporter();
    return this.commandFactory.helpCommand(reporter, parser);
  }
}
