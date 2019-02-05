package info.javaspec.example;

import static info.javaspec.lang.lambda.FunctionalDsl.describe;
import static info.javaspec.lang.lambda.FunctionalDsl.it;

public final class OneSpies {
  public static int numTimesRan = 0;

  {
    describe("OneSpies", () -> {
      it("does a thing", () -> numTimesRan++);
    });
  }
}
