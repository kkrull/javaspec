package info.javaspec.console;

import info.javaspec.LambdaSpec;
import info.javaspec.SpecReporter;

import java.io.PrintStream;

public class ConsoleReporter implements SpecReporter {
  private final PrintStream output;
  private int numStarted;
  private int numFailed;
  private int numPassed;

  public ConsoleReporter(PrintStream output) {
    this.output = output;
  }

  @Override
  public boolean hasFailingSpecs() {
    return false;
  }

  @Override
  public void runStarting() { }

  @Override
  public void specStarting(LambdaSpec spec) {
    this.numStarted++;
  }

  @Override
  public void specFailed(LambdaSpec spec) { }

  @Override
  public void specPassed(LambdaSpec spec) {
    this.numPassed++;
  }

  @Override
  public void runFinished() {
    this.output.printf(
      "Passed: %d\tFailed: %d\tTotal: %d\n",
      this.numPassed,
      this.numFailed,
      this.numStarted
    );
  }
}
