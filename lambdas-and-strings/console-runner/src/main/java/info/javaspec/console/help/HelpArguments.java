package info.javaspec.console.help;

import com.beust.jcommander.Parameter;
import info.javaspec.console.Command;
import info.javaspec.console.CommandFactory;
import info.javaspec.console.Reporter;
import info.javaspec.console.ReporterFactory;

import java.util.List;

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

  public Command parseCommand(List<String> stringArguments) {
    Reporter reporter = this.reporterFactory.plainTextReporter();
    if(stringArguments.isEmpty())
      return this.commandFactory.helpCommand(reporter);

    return this.commandFactory.helpCommand(reporter, stringArguments.get(0));
  }
}
