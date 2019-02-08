package info.javaspec.example;

import static info.javaspec.lang.lambda.FunctionalDsl.describe;
import static info.javaspec.lang.lambda.FunctionalDsl.it;

public class OneOfEachResult {
  {
    describe("OneOfEachResult", () -> {
      it("passes", () -> {});

      it("fails", () -> {
        throw new AssertionError("bang!");
      });
    });
  }
}
