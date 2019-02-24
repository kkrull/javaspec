package info.javaspec.console;

import static info.javaspec.lang.lambda.FunctionalDsl.describe;
import static info.javaspec.lang.lambda.FunctionalDsl.it;

public final class MainStepsOneFails {{
  describe("MainStepsOneFails", () -> {
    it("fails", () -> { throw new AssertionError("bang!"); });
  });
}}
