package info.javaspec.console;

import info.javaspec.Spec;
import info.javaspec.SpecCollection;

import java.io.PrintStream;
import java.util.List;

final class ConsoleReporter implements Reporter {
  private final PrintStream output;
  private int numStarted;
  private int numFailed;
  private int numPassed;

  public ConsoleReporter(PrintStream output) {
    this.output = output;
  }

  /* HelpObserver */

  @Override
  public void writeMessage(List<String> lines) {
    throw new UnsupportedOperationException();
  }

  /* RunObserver */

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
  public void collectionStarting(SpecCollection collection) {
    this.output.println(collection.description());
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
