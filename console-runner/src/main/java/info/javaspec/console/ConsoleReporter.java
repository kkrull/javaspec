package info.javaspec.console;

import info.javaspec.Spec;
import info.javaspec.SpecCollection;

import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

final class ConsoleReporter implements Reporter {
  private final PrintStream output;
  private final Deque<ReporterScope> scopes;
  private boolean hasEverPrintedAnything;

  private int numSpecsFailed;
  private int numSpecsPassed;
  private int numSpecsTotal;

  public ConsoleReporter(PrintStream output) {
    this.output = output;
    this.scopes = new ArrayDeque<>();

    this.hasEverPrintedAnything = false;
    this.numSpecsFailed = 0;
    this.numSpecsPassed = 0;
    this.numSpecsTotal = 0;
  }

  /* HelpObserver */

  @Override
  public void writeMessage(List<String> lines) {
    lines.forEach(this.output::println);
  }

  /* RunObserver */

  @Override
  public void beginCollection(SpecCollection collection) {
    ReporterScope reportCurrentEvent = scopeForCurrentEvents();
    reportCurrentEvent.beginCollection(collection);
    this.hasEverPrintedAnything = true;

    ReporterScope reportFutureEvents = ReporterScope.forCollection(collection, reportCurrentEvent);
    this.scopes.addLast(reportFutureEvents);
  }

  @Override
  public void endCollection(SpecCollection collection) {
    this.scopes.removeLast();
  }

  @Override
  public boolean hasFailingSpecs() {
    return this.numSpecsFailed > 0;
  }

  @Override
  public void runStarting() {
    this.scopes.addLast(ReporterScope.forRoot(this.output));
  }

  @Override
  public void runFinished() {
    if(this.hasEverPrintedAnything)
      this.output.println();

    this.output.println(String.format(
      "[Testing complete] Passed: %d, Failed: %d, Total: %d",
      this.numSpecsPassed,
      this.numSpecsFailed,
      this.numSpecsTotal
    ));
  }

  @Override
  public void specStarting(Spec spec) {
    scopeForCurrentEvents().specStarting(spec);
    this.numSpecsTotal++;
  }

  @Override
  public void specFailed(Spec spec) {
    scopeForCurrentEvents().specFailed(spec);
    this.hasEverPrintedAnything = true;
    this.numSpecsFailed++;
  }

  @Override
  public void specPassed(Spec spec) {
    scopeForCurrentEvents().specPassed(spec);
    this.hasEverPrintedAnything = true;
    this.numSpecsPassed++;
  }

  private ReporterScope scopeForCurrentEvents() {
    return this.scopes.peekLast();
  }
}
