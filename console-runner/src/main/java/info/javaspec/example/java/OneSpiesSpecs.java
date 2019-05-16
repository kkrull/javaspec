package info.javaspec.example.java;

import static info.javaspec.lang.lambda.FunctionalDsl.describe;
import static info.javaspec.lang.lambda.FunctionalDsl.it;

public final class OneSpiesSpecs {
  public static int numTimesRan = 0;

  {
    describe("OneSpies", () -> {
      it("does a thing", () -> numTimesRan++);
    });
  }
}
