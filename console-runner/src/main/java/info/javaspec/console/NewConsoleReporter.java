package info.javaspec.console;

import info.javaspec.Spec;
import info.javaspec.SpecCollection;

import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

final class NewConsoleReporter implements Reporter {
  private final PrintStream output;
  private final Deque<CollectionScope> scopes;

  private boolean hasPrintedAnyLines;
  private String specIndentation;
  private int numCollectionsInScope;

  public NewConsoleReporter(PrintStream output) {
    this.output = output;
    this.scopes = new ArrayDeque<>();
    this.hasPrintedAnyLines = false;
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
    CollectionScope containingScope = this.scopes.peekLast();
    if(containingScope.hasPrintedAnything())
      this.output.println();

    containingScope.beginCollection(collection);
    this.hasPrintedAnyLines = true;
    this.numCollectionsInScope += 1;

    if(this.numCollectionsInScope > 1)
      this.specIndentation += "  ";

    CollectionScope newScope = CollectionScope.forCollection(collection, containingScope);
    this.scopes.addLast(newScope);
  }

  @Override
  public void endCollection(SpecCollection collection) {
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
    this.scopes.addLast(CollectionScope.forRoot(this.output));
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

  private static final class CollectionScope {
    private final PrintStream output;
    private final String collectionIndentation;
    private int numItemsPrinted;

    public static CollectionScope forCollection(SpecCollection collection, CollectionScope parentScope) {
      return new CollectionScope(parentScope.output, parentScope.collectionIndentation + "  ");
    }

    public static CollectionScope forRoot(PrintStream output) {
      return new CollectionScope(output, "");
    }

    private CollectionScope(PrintStream output, String collectionIndentation) {
      this.output = output;
      this.collectionIndentation = collectionIndentation;
      this.numItemsPrinted = 0;
    }

    public void beginCollection(SpecCollection collection) {
      this.output.println(this.collectionIndentation + collection.description());
      somethingPrinted();
    }

    public boolean hasPrintedAnything() {
      return this.numItemsPrinted > 0;
    }

    public void somethingPrinted() {
      this.numItemsPrinted++;
    }
  }
}
