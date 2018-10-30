package info.javaspec.example;

import static info.javaspec.console.FunctionalDsl.it;

public class OneFails {
  {
    it("fails", () -> {
      throw new AssertionError("bang!");
    });
  }
}
