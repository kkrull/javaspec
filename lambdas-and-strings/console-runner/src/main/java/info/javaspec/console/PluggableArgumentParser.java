package info.javaspec.console;

import com.beust.jcommander.JCommander;

import java.util.List;

public class PluggableArgumentParser implements Main.ArgumentParser {
  private final CommandArguments baseArguments;
  private final CommandArguments[] commandArguments;

  public PluggableArgumentParser(CommandArguments baseArguments, CommandArguments... commandArguments) {
    this.baseArguments = baseArguments;
    this.commandArguments = commandArguments;
  }

  @Override
  public Command parseCommand(List<String> arguments) {
    JCommander parser = JCommander.newBuilder()
      .build();

    return this.commandArguments[0].makeCommand();
  }

  //JCommander object for one sub-command that generates an executable Command for JavaSpec
  @FunctionalInterface
  public interface CommandArguments {
    Command makeCommand();
  }
}
