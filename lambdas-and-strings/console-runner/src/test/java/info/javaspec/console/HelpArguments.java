package info.javaspec.console;

import com.beust.jcommander.Parameter;

import java.util.List;

public class HelpArguments {
  @Parameter(description = "show a list of commands, or help on a specific command")
  public String forCommandNamed;
}
