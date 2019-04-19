package info.javaspec.console;

import info.javaspec.Spec;
import info.javaspec.SpecCollection;

import java.io.PrintStream;
import java.util.List;

final class NewConsoleReporter implements Reporter {
  private final PrintStream output;
  private boolean hasPrintedAnyLines;
  private String collectionIndentation;
  private String specIndentation;
  private int numCollectionsInScope;

  public NewConsoleReporter(PrintStream output) {
    this.output = output;
    this.hasPrintedAnyLines = false;
    this.collectionIndentation = "";
    this.specIndentation = "";
    this.numCollectionsInScope = 0;
  }

  /* HelpObserver */

  @Override
  public void writeMessage(List<String> lines) {
    lines.forEach(this.output::println);
  }

  /* RunObserver */

  @Override
  public void beginCollection(SpecCollection collection) {
    this.numCollectionsInScope += 1;

    this.output.println(this.collectionIndentation + collection.description());
    this.hasPrintedAnyLines = true;

    this.collectionIndentation += "  ";
    if(this.numCollectionsInScope > 1)
      this.specIndentation += "  ";
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
  public void runFinished() {
    if(this.hasPrintedAnyLines)
      this.output.println();

    this.output.println("[Testing complete] Passed: 0, Failed: 0, Total: 0");
  }

  @Override
  public void specStarting(Spec spec) {
    this.output.print(this.specIndentation + "- " + spec.intendedBehavior());
  }

  @Override
  public void specFailed(Spec spec) {
    this.output.println(": FAIL");
    this.hasPrintedAnyLines = true;
  }

  @Override
  public void specPassed(Spec spec) {
    this.output.println(": PASS");
    this.hasPrintedAnyLines = true;
  }
}
