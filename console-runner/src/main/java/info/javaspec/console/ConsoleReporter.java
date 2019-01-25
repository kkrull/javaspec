package info.javaspec.console;

import info.javaspec.Spec;
import info.javaspec.SpecReporter;
import info.javaspec.Suite;

import java.io.PrintStream;

class ConsoleReporter implements SpecReporter {
  private final PrintStream output;
  private int numStarted;
  private int numFailed;
  private int numPassed;

  public ConsoleReporter(PrintStream output) {
    this.output = output;
  }

  @Override
  public boolean hasFailingSpecs() {
    return numFailed > 0;
  }

  @Override
  public void runStarting() { }

  @Override
  public void specStarting(Spec spec) {
    this.numStarted++;
    this.output.print(spec.intendedBehavior());
  }

  @Override
  public void specFailed(Spec spec) {
    this.numFailed++;
    this.output.println(": FAIL");
  }

  @Override
  public void specPassed(Spec spec) {
    this.numPassed++;
    this.output.println(": PASS");
  }

  @Override
  public void suiteStarting(Suite suite) {
    this.output.println(suite.description());
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
