package info.javaspec.spec;

import org.junit.runner.Description;

import static org.mockito.Mockito.*;

public final class MockSpec {
  public static Spec thatDies() {
    Spec spec = anyValid();

    try {
      doThrow(new RuntimeException("bang!")).when(spec).run();
    } catch(Exception e) {
      throw new AssertionError("Failed test setup", e);
    }

    return spec;
  }

  public static Spec withDescription(Description description) {
    Spec spec = anyValid();
    when(spec.getDescription()).thenReturn(description);
    return spec;
  }

  public static Spec anyValid() {
    return mock(Spec.class);
  }
}
