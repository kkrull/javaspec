package info.javaspec.console.help;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import info.javaspec.console.Command;
import info.javaspec.console.CommandFactory;
import info.javaspec.console.MultiCommandParser;
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
  public Command toExecutableCommand(JCommander parser) {
    HelpObserver observer = this.reporterFactory.plainTextReporter();
    return Optional.ofNullable(this.forCommandNamed)
      .map(helpOnWhat -> this.commandFactory.helpCommand(observer, helpOnWhat))
      .orElseGet(() -> this.commandFactory.helpCommand(observer, parser));
  }
}
