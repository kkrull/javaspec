package info.javaspec.console;

import com.beust.jcommander.JCommander;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MultiCommandParser implements Main.ArgumentParser {
  private final JCommander.Builder jCommanderBuilder;
  private final JCommanderArguments main;
  private final Map<String, JCommanderArguments> commandArgs;

  public MultiCommandParser(JCommanderArguments main) {
    this.main = main;
    this.commandArgs = new LinkedHashMap<>();
    this.jCommanderBuilder = JCommander.newBuilder()
      .addObject(this.main);
  }

  public void addCliCommand(String command, JCommanderArguments jCommanderArgs) {
    this.commandArgs.put(command, jCommanderArgs);
    this.jCommanderBuilder.addCommand(command, jCommanderArgs);
  }

  @Override
  public Command parseCommand(List<String> arguments) {
    JCommander jCommander = this.jCommanderBuilder.build();
    jCommander.parse(arguments.toArray(new String[0]));

    String selectedCommand = jCommander.getParsedCommand();
    JCommanderArguments selectedArgs = this.commandArgs.getOrDefault(selectedCommand, this.main);
    return selectedArgs.toExecutableCommand();
  }

  public interface JCommanderArguments {
    Command toExecutableCommand();
  }
}
