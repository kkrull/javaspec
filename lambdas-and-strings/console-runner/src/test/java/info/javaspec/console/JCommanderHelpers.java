package info.javaspec.console;

import com.beust.jcommander.JCommander;
import info.javaspec.console.MultiCommandParser.JCommanderParameters;

import java.util.List;

public final class JCommanderHelpers {
  private JCommanderHelpers() {}

  public static void parseMainArgs(JCommanderParameters subject, String... args) {
    JCommander jCommander = JCommander.newBuilder()
      .addObject(subject)
      .build();
    jCommander.parse(args);
  }

  public static void parseCommandArgs(JCommanderParameters subject, String commandName, List<String> commandArgs) {
    JCommander jCommander = JCommander.newBuilder()
      .addObject(emptyMainParameters(commandName))
      .addCommand(commandName, subject)
      .build();
    jCommander.parse(concat(commandName, commandArgs));
  }

  public static void parseCommandArgs(JCommanderParameters subject, String commandName, String... commandArgs) {
    JCommander jCommander = JCommander.newBuilder()
      .addObject(emptyMainParameters(commandName))
      .addCommand(commandName, subject)
      .build();
    jCommander.parse(concat(commandName, commandArgs));
  }

  private static JCommanderParameters emptyMainParameters(String expectedCommand) {
    return () -> {
      String message = String.format("Expected command named %s to be parsed, not the main command", expectedCommand);
      throw new AssertionError(message);
    };
  }

  private static String[] concat(String first, String[] rest) {
    String[] commandInvocation = new String[rest.length + 1];
    commandInvocation[0] = first;
    System.arraycopy(rest, 0, commandInvocation, 1, rest.length);
    return commandInvocation;
  }

  private static String[] concat(String first, List<String> rest) {
    String[] commandInvocation = new String[rest.size() + 1];
    commandInvocation[0] = first;

    int r = 0;
    for(String restElement : rest) {
      commandInvocation[r + 1] = restElement;
      r++;
    }

    return commandInvocation;
  }
}
