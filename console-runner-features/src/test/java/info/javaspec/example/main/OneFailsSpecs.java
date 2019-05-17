package info.javaspec.example.main;

import static info.javaspec.lang.lambda.FunctionalDsl.describe;
import static info.javaspec.lang.lambda.FunctionalDsl.it;

public final class OneFailsSpecs {
  {
    describe("OneFails", () -> {
      it("fails", () -> { throw new AssertionError("bang!"); });
    });
  }
}
