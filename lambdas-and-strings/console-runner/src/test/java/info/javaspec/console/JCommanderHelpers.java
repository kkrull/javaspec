package info.javaspec.console;

import com.beust.jcommander.JCommander;
import info.javaspec.console.MultiCommandParser.JCommanderParameters;

public final class JCommanderHelpers {
  private JCommanderHelpers() {}

  public static void parseMainArgs(JCommanderParameters subject, String... args) {
    JCommander jCommander = JCommander.newBuilder()
      .addObject(subject)
      .build();
    jCommander.parse(args);
  }

  public static void parseCommandArgs(JCommanderParameters subject, String commandName, String... commandArgs) {
    JCommanderParameters mainParameters = () -> {
      String message = String.format("Expected command named %s to be parsed, not the main command", commandName);
      throw new AssertionError(message);
    };

    JCommander jCommander = JCommander.newBuilder()
      .addObject(mainParameters)
      .addCommand(commandName, subject)
      .build();
    jCommander.parse(concat(commandName, commandArgs));
  }

  private static String[] concat(String first, String[] rest) {
    String[] commandInvocation = new String[rest.length + 1];
    commandInvocation[0] = first;
    System.arraycopy(rest, 0, commandInvocation, 1, rest.length);
    return commandInvocation;
  }
}
