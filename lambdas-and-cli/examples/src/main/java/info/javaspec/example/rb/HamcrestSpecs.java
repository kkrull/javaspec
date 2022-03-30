package info.javaspec.example.rb;

import org.hamcrest.Matchers;

import static info.javaspec.lang.lambda.FunctionalDsl.describe;
import static info.javaspec.lang.lambda.FunctionalDsl.it;
import static org.hamcrest.MatcherAssert.assertThat;

public class HamcrestSpecs {
  {
    describe("Hamcrest", () -> {
      it("passes", () -> assertThat("42", Matchers.equalTo("42")));
    });
  }
}
