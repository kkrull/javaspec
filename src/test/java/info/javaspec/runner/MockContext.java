package info.javaspec.runner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class MockContext {
  public static Context withSpecs() {
    Context context = anyValid();
    when(context.hasSpecs()).thenReturn(true);
    when(context.numSpecs()).thenReturn(1L);
    return context;
  }

  public static Context anyValid() {
    return mock(Context.class);
  }

  private MockContext() { /* static class */ }
}
