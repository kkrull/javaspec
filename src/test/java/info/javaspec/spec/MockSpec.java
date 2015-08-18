package info.javaspec.spec;

import org.junit.runner.Description;
import org.mockito.Mockito;

import java.util.Optional;

import static org.mockito.Mockito.mock;

public final class MockSpec {
  public static Builder that() { return new Builder(); }

  public static final class Builder {
    private final Spec spec = mock(Spec.class);
    private Optional<Description> description = Optional.empty();
    private Optional<Throwable> runException = Optional.empty();

    private Builder() { /* empty */ }

    public Builder hasDescription(Description description) {
      this.description = Optional.of(description);
      return this;
    }

    public Builder diesWhenRun() { return diesWhenRun(new RuntimeException("bang!")); }

    public Builder diesWhenRun(Throwable ex) {
      this.runException = Optional.of(ex);
      return this;
    }

    public Spec build() {
      description.ifPresent(x -> Mockito.when(spec.getDescription()).thenReturn(x));
      runException.ifPresent(this::stubRunToThrow);
      return spec;
    }

    private void stubRunToThrow(Throwable ex) {
      try {
        Mockito.doThrow(ex).when(spec).run();
      } catch(Exception e) {
        throw new AssertionError("Test setup failed", e);
      }
    }
  }
}
