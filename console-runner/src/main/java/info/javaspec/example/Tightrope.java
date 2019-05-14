package info.javaspec.example;

import static info.javaspec.lang.lambda.FunctionalDsl.describe;
import static info.javaspec.lang.lambda.FunctionalDsl.it;

public class Tightrope {
  {
    describe("Tightrope", () -> {
      it("sags to the ground, when a coyote with an anvil is standing on it", () -> { });
      it("recoils when the coyote drops the anvil", () -> { });
    });
  }
}
