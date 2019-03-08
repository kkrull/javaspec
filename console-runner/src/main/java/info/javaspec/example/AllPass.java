package info.javaspec.example;

import static info.javaspec.lang.lambda.FunctionalDsl.describe;
import static info.javaspec.lang.lambda.FunctionalDsl.it;

public class AllPass {
  {
    describe("AllPass", () -> {
      it("passes", () -> {});
    });
  }
}
