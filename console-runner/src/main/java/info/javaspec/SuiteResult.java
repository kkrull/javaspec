package info.javaspec;

enum SuiteResult {
  ALL_SPECS_PASSED(0),
  ONE_OR_MORE_SPECS_FAILED(1);

  private final int exitCode;

  SuiteResult(int exitCode) {
    this.exitCode = exitCode;
  }

  public void doExit(ExitHandler system) {
    system.exit(this.exitCode);
  }
}
