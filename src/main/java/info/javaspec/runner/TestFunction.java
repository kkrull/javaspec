package info.javaspec.runner;

import info.javaspec.dsl.Before;
import info.javaspec.dsl.Cleanup;
import info.javaspec.dsl.It;

import java.util.List;

final class TestFunction {
  public final It assertion;
  public final List<Before> befores;
  public final List<Cleanup> afters;

  public TestFunction(It assertion, List<Before> befores, List<Cleanup> afters) {
    this.assertion = assertion;
    this.befores = befores;
    this.afters = afters;
  }

  public boolean hasUnassignedFunctions() {
    return assertion == null || befores.contains(null) || afters.contains(null);
  }
}
