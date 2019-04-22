package info.javaspec.console;

import info.javaspec.Spec;
import info.javaspec.SpecCollection;

import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

final class NewConsoleReporter implements Reporter {
  private final PrintStream output;
  private final Deque<ReporterScope> scopes;
  private boolean hasEverPrintedAnything;

  public NewConsoleReporter(PrintStream output) {
    this.output = output;
    this.scopes = new ArrayDeque<>();
    this.hasEverPrintedAnything = false;
  }

  /* HelpObserver */

  @Override
  public void writeMessage(List<String> lines) {
    lines.forEach(this.output::println);
  }

  /* RunObserver */

  @Override
  public void beginCollection(SpecCollection collection) {
    ReporterScope containingScope = this.scopes.peekLast();
    containingScope.beginCollection(collection);
    this.hasEverPrintedAnything = true;

    ReporterScope newScope = ReporterScope.forCollection(collection, containingScope);
    this.scopes.addLast(newScope);
  }

  @Override
  public void endCollection(SpecCollection collection) {
    this.scopes.removeLast();
  }

  @Override
  public boolean hasFailingSpecs() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void runStarting() {
    this.scopes.addLast(ReporterScope.forRoot(this.output));
  }

  @Override
  public void runFinished() {
    if(this.hasEverPrintedAnything)
      this.output.println();

    this.output.println("[Testing complete] Passed: 0, Failed: 0, Total: 0");
  }

  @Override
  public void specStarting(Spec spec) {
    this.scopes.peekLast().specStarting(spec);
  }

  @Override
  public void specFailed(Spec spec) {
    this.scopes.peekLast().specFailed(spec);
    this.hasEverPrintedAnything = true;
  }

  @Override
  public void specPassed(Spec spec) {
    this.scopes.peekLast().specPassed(spec);
    this.hasEverPrintedAnything = true;
  }
}
