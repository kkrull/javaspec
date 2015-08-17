package info.javaspec.spec;

import org.junit.runner.Description;
import org.mockito.Mockito;

import static org.mockito.Mockito.mock;

public final class MockSpec {
  public static Spec withDescription(Description description) {
    Spec spec = anyValid();
    return spec;
  }

  public static Spec anyValid() {
    return mock(Spec.class);
  }
}
