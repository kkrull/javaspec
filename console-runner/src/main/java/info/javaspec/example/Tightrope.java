package info.javaspec.example;

import static info.javaspec.lang.lambda.FunctionalDsl.describe;
import static info.javaspec.lang.lambda.FunctionalDsl.it;

public class Tightrope {
  {
    describe("Tightrope", () -> {
      it("supports a coyote holding an anvil", () -> {
        throw new AssertionError("tightrope stretched down to the ground");
      });

      it("recoils when the coyote drops the anvil", () -> { });
    });
  }
}
