package info.javaspec.console;

import info.javaspec.Spec;
import info.javaspec.SpecCollection;

import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

final class NewConsoleReporter implements Reporter {
  private final PrintStream output;
  private final Deque<Scope> scopes;

  private boolean hasPrintedAnyLines;
  private String collectionIndentation;
  private String specIndentation;
  private int numCollectionsInScope;

  public NewConsoleReporter(PrintStream output) {
    this.output = output;
    this.scopes = new ArrayDeque<>();
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
    if(this.scopes.peekLast().hasPrintedAnything())
      this.output.println();

    this.output.println(this.collectionIndentation + collection.description());
    this.scopes.peekLast().somethingPrinted();
    this.hasPrintedAnyLines = true;
    this.numCollectionsInScope += 1;

    this.collectionIndentation += "  ";
    if(this.numCollectionsInScope > 1)
      this.specIndentation += "  ";

    this.scopes.addLast(Scope.forCollection(collection));
  }

  @Override
  public void endCollection(SpecCollection collection) {
    this.collectionIndentation = this.collectionIndentation.substring(0, this.collectionIndentation.length() - 2);
    this.specIndentation = this.specIndentation.substring(0, Math.max(0, this.specIndentation.length() - 2));
    this.numCollectionsInScope -= 1;
    this.scopes.removeLast();
  }

  @Override
  public boolean hasFailingSpecs() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void runStarting() {
    this.scopes.addLast(Scope.forRoot());
  }

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

  private static final class Scope {
    private final String source;
    private int numItemsPrinted;

    public static Scope forRoot() {
      return new Scope("root");
    }

    public static Scope forCollection(SpecCollection collection) {
      return new Scope(collection.description());
    }

    private Scope(String source) {
      this.source = source;
      this.numItemsPrinted = 0;
    }

    public boolean hasPrintedAnything() {
      return this.numItemsPrinted > 0;
    }

    public void somethingPrinted() {
      this.numItemsPrinted++;
    }
  }
}
