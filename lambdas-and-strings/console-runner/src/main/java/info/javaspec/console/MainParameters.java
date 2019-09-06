package info.javaspec.console;

import com.beust.jcommander.Parameter;

public final class MainParameters implements MultiCommandParser.JCommanderParameters {
  private final CommandFactory commandFactory;
  private final ReporterFactory reporterFactory;

  @Parameter(
    names = "--help",
    help = true
  )
  private boolean isAskingForHelp;

  public MainParameters(CommandFactory commandFactory, ReporterFactory reporterFactory) {
    this.commandFactory = commandFactory;
    this.reporterFactory = reporterFactory;
  }

  @Override
  public Command toExecutableCommand() {
    Reporter reporter = this.reporterFactory.plainTextReporter();
    return this.commandFactory.helpCommand(reporter);
  }
}
