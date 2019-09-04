package info.javaspec.console;

import com.beust.jcommander.ParameterException;

final class Exceptions {
  static final class CommandAlreadyAdded extends RuntimeException {
    public static CommandAlreadyAdded named(String command) {
      return new CommandAlreadyAdded(String.format("Command has already been added: %s", command));
    }

    private CommandAlreadyAdded(String message) {
      super(message);
    }
  }

  static final class InvalidArguments extends RuntimeException {
    public static InvalidArguments dueTo(ParameterException cause) {
      return new InvalidArguments(cause.getMessage());
    }

    private InvalidArguments(String message) {
      super(message);
    }
  }
}
