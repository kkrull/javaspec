package info.javaspec.console;

import com.beust.jcommander.JCommander;

import java.util.List;

public class MultiCommandParser implements Main.ArgumentParser {
  private final JCommanderArguments main;

  public MultiCommandParser(JCommanderArguments main) {
    this.main = main;
  }

  @Override
  public Command parseCommand(List<String> arguments) {
    JCommander parser = JCommander.newBuilder()
      .addObject(this.main)
      .build();
    parser.parse(arguments.toArray(new String[0]));
    return this.main.toExecutableCommand();
  }

  public interface JCommanderArguments {
    Command toExecutableCommand();
  }
}
