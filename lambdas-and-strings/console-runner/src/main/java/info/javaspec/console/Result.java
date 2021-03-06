package info.javaspec.console;

public class Result {
  public final int exitCode;
  private final String summary;
  public final Exception exception;

  public static Result failure(int exitCode, Exception exception) {
    return new Result(exitCode, null, exception);
  }

  public static Result failure(int exitCode, String summary) {
    return new Result(exitCode, summary, null);
  }

  public static Result success() {
    return new Result(0, null, null);
  }

  private Result(int exitCode, String summary, Exception exception) {
    this.exitCode = exitCode;
    this.summary = summary;
    this.exception = exception;
  }

  public void reportTo(Reporter reporter) {
    if(this.exception == null)
      return;

    reporter.commandFailed(this.exception);
  }

  public String summary() {
    return this.summary;
  }
}
