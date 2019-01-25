package info.javaspec.example;

import static info.javaspec.lang.lambda.FunctionalDsl.it;

public final class OneSpies {
  public static int numTimesRan = 0;

  {
    it("does a thing", () -> numTimesRan++); //TODO KDK: Should it be possible to have it without a top-level describe?
  }
}
