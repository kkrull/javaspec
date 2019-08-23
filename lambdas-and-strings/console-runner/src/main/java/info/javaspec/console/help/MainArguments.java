package info.javaspec.console.help;

import info.javaspec.console.Command;
import info.javaspec.console.CommandFactory;
import info.javaspec.console.ReporterFactory;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class MainArguments {
  private final CommandFactory commandFactory;
  private final ReporterFactory reporterFactory;

  public MainArguments(CommandFactory commandFactory, ReporterFactory reporterFactory) {
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
}
