package info.javaspec.console.help;

import com.beust.jcommander.Parameter;
import info.javaspec.console.Command;
import info.javaspec.console.CommandFactory;

public final class HelpArguments {
  private final CommandFactory commandFactory;

  @Parameter()
  public String forCommandNamed;

  public HelpArguments(CommandFactory commandFactory) {
    this.commandFactory = commandFactory;
  }

  public boolean hasCommandParameter() {
    return this.forCommandNamed != null;
  }

  public Command parseCommand() {
    return this.commandFactory.helpCommand(null);
  }
}
