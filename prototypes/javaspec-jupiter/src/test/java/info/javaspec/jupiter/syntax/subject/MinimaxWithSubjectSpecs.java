package info.javaspec.jupiter.syntax.subject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

@DisplayName("Subject syntax: Try Minimax")
class MinimaxWithSubjectSpecs {
  @TestFactory DynamicNode makeSpecs() {
    JavaSpec<Minimax> javaspec = new JavaSpec<>();
    return javaspec.describe(Minimax.class, () -> {
      javaspec.pending("scores a game ending in a draw as 0");
    });
  }

  private static final class Minimax {}
}
