package info.javaspec.console;

import com.beust.jcommander.JCommander;

public final class JCommanderHelpers {
  private JCommanderHelpers() {}

  public static void parseArgs(MultiCommandParser.JCommanderParameters subject, String... args) {
    JCommander jCommander = JCommander.newBuilder()
      .addObject(subject)
      .build();
    jCommander.parse(args);
  }
}
