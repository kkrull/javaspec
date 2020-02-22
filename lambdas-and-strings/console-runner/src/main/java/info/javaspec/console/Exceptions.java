package info.javaspec.console;

import com.beust.jcommander.ParameterException;

public final class Exceptions {
  static final class CommandAlreadyAdded extends RuntimeException {
    public static CommandAlreadyAdded named(String command) {
      return new CommandAlreadyAdded(String.format("Command has already been added: %s", command));
    }

    private CommandAlreadyAdded(String message) {
      super(message);
    }
  }

  public static final class InvalidArguments extends RuntimeException {
    public static InvalidArguments dueTo(ParameterException cause) {
      return new InvalidArguments(cause.getMessage(), cause);
    }

    public static InvalidArguments forCommand(String command, ParameterException cause) {
      return new InvalidArguments(command + ": " + cause.getMessage(), cause);
    }

    private InvalidArguments(String message, ParameterException cause) {
      super(message, cause);
    }
  }
}
