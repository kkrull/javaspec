package info.javaspec.console;

import info.javaspec.Spec;
import info.javaspec.SpecCollection;

import java.io.PrintStream;
import java.util.List;

class ReporterState implements Reporter {
  private final PrintStream output;
  private final String indentCollections;
  private final String indentSpecs;
  private final boolean isRoot;
  private final String source;

  private boolean hasPrintedAnyLines;

  public static ReporterState newRoot(PrintStream output) {
    return new ReporterState(output, "", "", true, "<root>");
  }

  private ReporterState(PrintStream output, String indentCollections, String indentSpecs, boolean isRoot, String source) {
    this.output = output;
    this.indentCollections = indentCollections;
    this.indentSpecs = indentSpecs;
    this.isRoot = isRoot;
    this.source = source;
    this.hasPrintedAnyLines = false;
  }

  public ReporterState createInnerScope(String source) {
    String indentSpecs = this.isRoot
      ? ""
      : this.indentSpecs + "  ";

    return new ReporterState(
      this.output,
      this.indentCollections + "  ",
      indentSpecs,
      false,
      source
    );
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

    this.output.println(this.indentCollections + collection.description());
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
    this.output.print(this.indentSpecs + "- ");
    this.output.print(spec.intendedBehavior());
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

  private void printMessage(String message) {
    this.output.println(message);
  }

  public void printSeparator() {
    this.output.println();
  }

  @Override
  public String toString() {
    return this.source;
  }
}
