package info.javaspec.spec;

import static org.mockito.Mockito.mock;

public final class MockSpec {
  public static Spec anyValid() {
    return mock(Spec.class);
  }
}
