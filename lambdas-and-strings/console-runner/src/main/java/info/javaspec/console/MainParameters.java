package info.javaspec.console;

import com.beust.jcommander.Parameter;
import info.javaspec.console.help.HelpArguments;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class MainParameters implements MultiCommandParser.JCommanderParameters {
  private final CommandFactory commandFactory;
  private final ReporterFactory reporterFactory;

  @Parameter(
    names = "--help",
    help = true
  )
  private boolean isAskingForHelp; //TODO KDK: Test

  public MainParameters(CommandFactory commandFactory, ReporterFactory reporterFactory) {
    this.commandFactory = commandFactory;
    this.reporterFactory = reporterFactory;
  }

  public Optional<Command> parseCommand(List<String> allArguments) {
    HelpArguments helpArguments = new HelpArguments(this.commandFactory, this.reporterFactory);
    if(allArguments.isEmpty())
      return Optional.of(helpArguments.parseCommand(Collections.emptyList()));
    else if(allArguments.equals(Collections.singletonList("--help")))
      return Optional.of(helpArguments.parseCommand(Collections.emptyList()));
    else
      return Optional.empty();
  }

  @Override
  public Command toExecutableCommand() { //TODO KDK: Test
    Reporter reporter = this.reporterFactory.plainTextReporter();
    if(this.isAskingForHelp)
      return this.commandFactory.helpCommand(reporter);
    else
      return this.commandFactory.helpCommand(reporter);
  }
}
