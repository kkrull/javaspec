package info.javaspec.example.rb;

import static info.javaspec.lang.lambda.FunctionalDsl.describe;
import static info.javaspec.lang.lambda.FunctionalDsl.it;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class HamcrestSpecs {
  {
    describe("Hamcrest", () -> {
      it("passes", () -> assertThat("42", equalTo("42")));
    });
  }
}
