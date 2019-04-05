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
    lines.forEach(this.output::println);
  }

  /* RunObserver */

  @Override
  public void collectionStarting(SpecCollection collection) {
    if(this.hasPrintedFirstCollection)
      this.output.println();

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
    this.output.println();
    this.output.println(String.format("[Testing complete] Passed: %d, Failed: %d, Total: %d",
      this.numPassed,
      this.numFailed,
      this.numStarted
    ));
  }

  @Override
  public void specStarting(Spec spec) {
    this.numStarted++;
    listItemPrint(spec.intendedBehavior());
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

  private void listItemPrint(String item) {
    this.output.print("- ");
    this.output.print(item);
  }
}
