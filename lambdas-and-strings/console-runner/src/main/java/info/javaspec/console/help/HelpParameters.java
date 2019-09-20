package info.javaspec.console.help;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import info.javaspec.console.Command;
import info.javaspec.console.CommandFactory;
import info.javaspec.console.MultiCommandParser;
import info.javaspec.console.ReporterFactory;

@Parameters(commandDescription = "List commands and how to use them")
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
    return this.commandFactory.helpCommand(observer, parser);
  }
}
