package info.javaspec.spec;

import info.javaspec.dsl.Before;
import info.javaspec.dsl.Cleanup;
import info.javaspec.dsl.It;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

import java.lang.reflect.Field;
import java.util.List;

import static java.util.stream.Collectors.toList;

final class FieldSpec extends Spec {
  private final Description testDescription;
  private SpecState state;

  FieldSpec(String id, Description testDescription, Field it, List<Field> beforeSpecFields, List<Field> afterSpecFields) {
    super(id);
    this.testDescription = testDescription;
    this.state = new DeclaredState(it, beforeSpecFields, afterSpecFields);
  }

  @Override
  public Description getDescription() { return testDescription; }

  @Override
  public void addDescriptionTo(Description suite) {
    suite.addChild(testDescription);
  }

  @Override
  public void run(RunNotifier notifier) {
    try {
      state = state.instantiate();
    } catch(Exception ex) {
      notifier.fireTestFailure(new Failure(getDescription(), ex));
      return;
    }

    state.run(notifier);
  }

  private final class DeclaredState implements SpecState {
    private final Field assertionField;
    private final List<Field> beforeSpecFields;
    private final List<Field> afterSpecFields;

    public DeclaredState(Field it, List<Field> beforeSpecFields, List<Field> afterSpecFields) {
      this.assertionField = it;
      this.beforeSpecFields = beforeSpecFields;
      this.afterSpecFields = afterSpecFields;
    }

    @Override
    public SpecState instantiate() {
      SpecExecutionContext context = SpecExecutionContext.forDeclaringClass(assertionField.getDeclaringClass());
      List<Before> beforeThunks = beforeSpecFields.stream()
        .map(x -> context.getAssignedValue(x, Before.class))
        .collect(toList());
      List<Cleanup> afterThunks = afterSpecFields.stream()
        .map(x -> context.getAssignedValue(x, Cleanup.class))
        .collect(toList());
      It assertionThunk = context.getAssignedValue(assertionField, It.class);

      if(beforeThunks.contains(null) || afterThunks.contains(null) || assertionThunk == null) {
        return new PendingState();
      } else {
        return new RunnableState(assertionThunk, beforeThunks, afterThunks);
      }
    }

    @Override
    public void run(RunNotifier notifier) {
      throw new IllegalStateException("Spec has not been instantiated yet by creating a runnable state");
    }
  }

  private final class PendingState implements SpecState {
    @Override
    public SpecState instantiate() { return this; }

    @Override
    public void run(RunNotifier notifier) {
      notifier.fireTestIgnored(getDescription());
    }
  }

  private final class RunnableState implements SpecState {
    private final It assertionThunk;
    private final List<Before> beforeThunks;
    private final List<Cleanup> afterThunks;

    public RunnableState(It assertionThunk, List<Before> beforeThunks, List<Cleanup> afterThunks) {
      this.assertionThunk = assertionThunk;
      this.beforeThunks = beforeThunks;
      this.afterThunks = afterThunks;
    }

    @Override
    public SpecState instantiate() { return this; }

    @Override
    public void run(RunNotifier notifier) {
      notifier.fireTestStarted(getDescription());

      try {
        beforeSpec();
        assertionThunk.run();
      } catch(Exception | AssertionError ex) {
        notifier.fireTestFailure(new Failure(getDescription(), ex));
        return;
      } finally {
        try {
          afterSpec();
        } catch(Exception | AssertionError ex) {
          notifier.fireTestFailure(new Failure(getDescription(), ex));
        }
      }

      notifier.fireTestFinished(getDescription());
    }

    private void beforeSpec() throws Exception {
      for(Before before : beforeThunks)
        before.run();
    }

    private void afterSpec() throws Exception {
      for(Cleanup after : afterThunks)
        after.run();
    }
  }

  private interface SpecState {
    SpecState instantiate();

    void run(RunNotifier notifier);
  }
}
