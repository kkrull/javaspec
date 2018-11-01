package info.javaspec.example;

import static info.javaspec.console.FunctionalDsl.it;

public class OneOfEachResult {
  {
    it("passes", () -> {});

    it("fails", () -> {
      throw new AssertionError("bang!");
    });
  }
}
