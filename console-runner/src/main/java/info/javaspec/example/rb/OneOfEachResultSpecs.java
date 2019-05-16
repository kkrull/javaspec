package info.javaspec.example.rb;

import static info.javaspec.lang.lambda.FunctionalDsl.describe;
import static info.javaspec.lang.lambda.FunctionalDsl.it;

public class OneOfEachResultSpecs {
  {
    describe("OneOfEachResult", () -> {
      it("passes", () -> {});

      it("fails", () -> {
        throw new AssertionError("bang!");
      });
    });
  }
}
