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
  private boolean hasPrintedFirstCollection;

  public ConsoleReporter(PrintStream output) {
    this.output = output;
    this.hasPrintedFirstCollection = false;
  }

  /* HelpObserver */

  @Override
  public void writeMessage(List<String> lines) {
    lines.forEach(this::printMessage);
  }

  /* RunObserver */

  @Override
  public void collectionStarting(SpecCollection collection) {
    if(this.hasPrintedFirstCollection)
      printSeparator();

    this.output.println(collection.description());
    this.hasPrintedFirstCollection = true;
  }

  @Override
  public boolean hasFailingSpecs() {
    return numFailed > 0;
  }

  @Override
  public void runStarting() { }

  @Override
  public void runFinished() {
    printSeparator();
    printMessage(
      "[Testing complete] Passed: %d, Failed: %d, Total: %d",
      this.numPassed,
      this.numFailed,
      this.numStarted
    );
  }

  @Override
  public void specStarting(Spec spec) {
    this.numStarted++;
    printListItem(spec.intendedBehavior());
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

  private void printListItem(String item) {
    this.output.print("- ");
    this.output.print(item);
  }

  private void printMessage(String format, Object... args) {
    printMessage(String.format(format, args));
  }

  private void printMessage(String message) {
    this.output.println(message);
  }

  private void printSeparator() {
    this.output.println();
  }
}
