package info.javaspec.console;

@FunctionalInterface
public interface Command {
  Result run();

  final class Result {
    public final int exitCode;
    public final Exception exception;

    public Result(int exitCode, Exception exception) {
      this.exitCode = exitCode;
      this.exception = exception;
    }

    public Result(int exitCode) {
      this.exitCode = exitCode;
      this.exception = null;
    }
  }
}
