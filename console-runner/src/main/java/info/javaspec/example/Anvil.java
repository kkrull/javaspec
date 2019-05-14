package info.javaspec.example;

import static info.javaspec.lang.lambda.FunctionalDsl.describe;
import static info.javaspec.lang.lambda.FunctionalDsl.it;

public class Anvil {
  {
    describe("Anvil", () -> {
      it("falls on a passing road runner", () -> {
        throw new AssertionError("levitates mid-air until an inquisitive coyote looks underneath it");
      });
    });
  }
}
