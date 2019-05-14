package info.javaspec.example;

import static info.javaspec.lang.lambda.FunctionalDsl.describe;
import static info.javaspec.lang.lambda.FunctionalDsl.it;

//https://en.wikipedia.org/wiki/Beep,_Beep_(film)
public class SpringOperatedBoxingGlove {
  {
    describe("Spring-operated boxing glove", () -> {
      describe("when the spring expands", () -> {
        it("pushes the rock holding it backwards", () -> { });
      });

      describe("when the spring contracts again", () -> {
        it("punches any nearby coyote in the face", () -> { });
      });
    });
  }
}
