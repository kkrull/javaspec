package info.javaspec.console.help;

import com.beust.jcommander.Parameter;

public final class HelpArguments {
  @Parameter()
  public String forCommandNamed;

  public boolean hasCommandParameter() {
    return this.forCommandNamed != null;
  }
}
