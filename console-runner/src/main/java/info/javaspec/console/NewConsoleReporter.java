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

  public NewConsoleReporter(PrintStream output) {
    this.output = output;
    this.scopes = new ArrayDeque<>();
    this.hasPrintedAnyLines = false;
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

    CollectionScope newScope = CollectionScope.forCollection(collection, containingScope);
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
    CollectionScope scope = this.scopes.peekLast();
    scope.specStarting(spec);
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
    private final boolean isRoot;
    private final PrintStream output;
    private final String collectionIndent;
    private final String specIndent;
    private int numItemsPrinted;

    public static CollectionScope forCollection(SpecCollection collection, CollectionScope parent) {
      String specIndent = parent.isRoot ? "" : parent.specIndent + "  ";
      return new CollectionScope(
        false,
        parent.output,
        parent.collectionIndent + "  ",
        specIndent
      );
    }

    public static CollectionScope forRoot(PrintStream output) {
      return new CollectionScope(
        true,
        output,
        "",
        ""
      );
    }

    private CollectionScope(boolean isRoot, PrintStream output, String collectionIndent, String specIndent) {
      this.isRoot = isRoot;
      this.output = output;
      this.collectionIndent = collectionIndent;
      this.specIndent = specIndent;
      this.numItemsPrinted = 0;
    }

    public void beginCollection(SpecCollection collection) {
      this.output.println(this.collectionIndent + collection.description());
      somethingPrinted();
    }

    public boolean hasPrintedAnything() {
      return this.numItemsPrinted > 0;
    }

    public void somethingPrinted() {
      this.numItemsPrinted++;
    }

    public void specStarting(Spec spec) {
      this.output.print(this.specIndent + "- " + spec.intendedBehavior());
    }
  }
}
