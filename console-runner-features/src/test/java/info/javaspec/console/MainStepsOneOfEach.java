package info.javaspec.console;

import java.util.LinkedList;
import java.util.List;

import static info.javaspec.lang.lambda.FunctionalDsl.describe;
import static info.javaspec.lang.lambda.FunctionalDsl.it;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

public final class MainStepsOneOfEach {
  private static final List<String> _specsRan = new LinkedList<>();

  public static void reset() {
    _specsRan.clear();
  }

  public static void specsShouldHaveRun() {
    assertThat(_specsRan, contains("passes", "fails"));
  }

  {
    describe("MainStepsOneOfEach", () -> {
      it("passes", () -> _specsRan.add("passes"));
      it("fails", () -> {
        _specsRan.add("fails");
        throw new AssertionError("bang!");
      });
    });
  }
}
