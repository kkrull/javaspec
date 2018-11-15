package info.javaspec.example;

import static info.javaspec.lang.lambda.FunctionalDsl.it;

public class OneFails {
  {
    it("fails", () -> {
      throw new AssertionError("bang!");
    });
  }
}
