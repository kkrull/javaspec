package info.javaspec.example.rb;

import static info.javaspec.lang.lambda.FunctionalDsl.describe;
import static info.javaspec.lang.lambda.FunctionalDsl.it;

//https://en.wikipedia.org/wiki/Beep,_Beep_(film)
public class BeepBeepSpecs {
  {
    describe("Spring-operated boxing glove", () -> {
      describe("when the spring expands", () -> {
        it("pushes the rock holding it backwards", () -> { });
      });

      describe("when the spring contracts again", () -> {
        it("punches any nearby coyote in the face", () -> { });
      });
    });

    describe("Tightrope", () -> {
      it("supports a coyote holding an anvil", () -> {
        throw new AssertionError("tightrope stretched down to the ground");
      });

      it("recoils when the coyote drops the anvil", () -> { });
    });
  }
}
