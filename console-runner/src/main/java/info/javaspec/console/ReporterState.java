package info.javaspec.console;

import info.javaspec.Spec;
import info.javaspec.SpecCollection;

import java.io.PrintStream;
import java.util.List;

class ReporterState implements Reporter {
  private final PrintStream output;
  private boolean hasPrintedAnyLines;

  public static ReporterState newRoot(PrintStream output) {
    return new ReporterState(output);
  }

  private ReporterState(PrintStream output) {
    this.output = output;
    this.hasPrintedAnyLines = false;
  }

  /* HelpObserver */

  public void writeMessage(String format, Object... args) {
    printMessage(String.format(format, args));
  }

  @Override
  public void writeMessage(List<String> lines) {
    lines.forEach(this::printMessage);
  }

  /* RunObserver */

  public void beginCollection(SpecCollection collection) {
    if(this.hasPrintedAnyLines)
      printSeparator();

    this.output.println(collection.description());
    this.hasPrintedAnyLines = true;
  }

  @Override
  public void endCollection(SpecCollection collection) { }

  @Override
  public boolean hasFailingSpecs() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void runStarting() { }

  @Override
  public void runFinished() { }

  @Override
  public void specStarting(Spec spec) {
    printListItem(spec.intendedBehavior());
  }

  @Override
  public void specPassed(Spec spec) {
    this.output.println(": PASS");
  }

  @Override
  public void specFailed(Spec spec) {
    this.output.println(": FAIL");
  }

  /* Generic */

  private void printListItem(String item) {
    this.output.print("- ");
    this.output.print(item);
  }

  private void printMessage(String message) {
    this.output.println(message);
  }

  public void printSeparator() {
    this.output.println();
  }
}
