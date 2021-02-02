package info.javaspec.jupiter.syntax.fixture;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

@DisplayName("Fixture syntax: Minimax")
class MinimaxSpecs {
  @TestFactory
  DynamicNode specs() {
    JavaSpec spec = new JavaSpec();
    return spec.describe(Minimax.class, () -> {
      spec.pending("exists");
    });
  }

  private static final class Minimax {
    
  }
}
