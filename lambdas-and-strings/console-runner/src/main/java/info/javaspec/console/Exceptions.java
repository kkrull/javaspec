package info.javaspec.console;

final class Exceptions {
  static final class CommandAlreadyAdded extends RuntimeException {
    public static CommandAlreadyAdded named(String command) {
      return new CommandAlreadyAdded(String.format("Command has already been added: %s", command));
    }

    private CommandAlreadyAdded(String message) {
      super(message);
    }
  }
}
