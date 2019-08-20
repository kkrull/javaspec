package info.javaspec.console.help;

import com.beust.jcommander.Parameter;
import info.javaspec.console.Command;
import info.javaspec.console.CommandFactory;
import info.javaspec.console.ReporterFactory;

public final class HelpArguments {
  private final CommandFactory commandFactory;
  private final ReporterFactory reporterFactory;

  @Parameter()
  public String forCommandNamed;

  public HelpArguments(CommandFactory commandFactory, ReporterFactory reporterFactory) {
    this.commandFactory = commandFactory;
    this.reporterFactory = reporterFactory;
  }

  public boolean hasCommandParameter() {
    return this.forCommandNamed != null;
  }

  public Command parseCommand() {
    return this.commandFactory.helpCommand(this.reporterFactory.plainTextReporter());
  }
}
