package info.javaspec.example.rb;

import static info.javaspec.lang.lambda.FunctionalDsl.describe;
import static info.javaspec.lang.lambda.FunctionalDsl.it;

public class CoyoteAnvilSpecs {
  {
    describe("Anvil (Coyote perspective)", () -> {
      it("falls onto a passing road runner", () -> {
        throw new AssertionError("The anvil was supposed to fall, but it is levitating in mid-air");
      });
    });
  }
}
