package info.javaspec.example.rb;

import java.util.LinkedList;
import java.util.List;

import static info.javaspec.lang.lambda.FunctionalDsl.describe;
import static info.javaspec.lang.lambda.FunctionalDsl.it;

public final class DescribeTwoSpecs {
  public static List<String> descriptionsRan = new LinkedList<>();

  {
    describe("Illudium Q-36 Explosive Space Modulator", () -> {
      it("discombobulates", () -> descriptionsRan.add("discombobulates"));
      it("explodes", () -> descriptionsRan.add("explodes"));
    });
  }
}
