package info.javaspec.console;

import info.javaspec.Spec;
import info.javaspec.SpecCollection;

import java.io.PrintStream;

final class ReporterScope {
  private final boolean isRoot;
  private final PrintStream output;
  private final String collectionIndent;
  private final String specIndent;
  private boolean hasPrintedAnyCollections;

  public static ReporterScope forCollection(SpecCollection collection, ReporterScope parent) {
    String indentSpecsOnlyInInnerCollections = parent.isRoot ? "" : parent.specIndent + "  ";
    return new ReporterScope(
      false,
      parent.output,
      parent.collectionIndent + "  ",
      indentSpecsOnlyInInnerCollections
    );
  }

  public static ReporterScope forRoot(PrintStream output) {
    return new ReporterScope(
      true,
      output,
      "",
      ""
    );
  }

  private ReporterScope(boolean isRoot, PrintStream output, String collectionIndent, String specIndent) {
    this.isRoot = isRoot;
    this.output = output;
    this.collectionIndent = collectionIndent;
    this.specIndent = specIndent;
    this.hasPrintedAnyCollections = false;
  }

  public void beginCollection(SpecCollection collection) {
    if(this.hasPrintedAnyCollections)
      this.output.println();

    this.output.println(this.collectionIndent + collection.description());
    this.hasPrintedAnyCollections = true;
  }

  public void specStarting(Spec spec) {
    this.output.print(this.specIndent + "- " + spec.intendedBehavior());
  }

  public void specFailed(Spec spec) {
    this.output.println(": FAIL");
  }

  public void specPassed(Spec spec) {
    this.output.println(": PASS");
  }
}
