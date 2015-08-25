package info.javaspec.spec;

import info.javaspec.dsl.Before;
import info.javaspec.dsl.Cleanup;
import info.javaspec.dsl.It;

import java.util.List;

final class RunnableSpec {
  private final It assertion;
  private final List<Before> befores;
  private final List<Cleanup> afters;

  public RunnableSpec(It assertion, List<Before> befores, List<Cleanup> afters) {
    this.assertion = assertion;
    this.befores = befores;
    this.afters = afters;
  }

  public boolean hasUnassignedFunctions() {
    return assertion == null || befores.contains(null) || afters.contains(null);
  }

  public void runBeforeSpec() throws Exception {
    for(Before before : befores) { before.run(); }
  }

  public void runSpec() throws Exception {
    assertion.run();
  }

  public void runAfterSpec() throws Exception {
    for(Cleanup after : afters) { after.run(); }
  }
}
