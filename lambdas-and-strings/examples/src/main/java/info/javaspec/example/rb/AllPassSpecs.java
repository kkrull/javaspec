package info.javaspec.example.rb;

import static info.javaspec.lang.lambda.FunctionalDsl.describe;
import static info.javaspec.lang.lambda.FunctionalDsl.it;

public class AllPassSpecs {
  {
    describe("AllPass", () -> {
      it("passes", () -> {});
    });
  }
}
