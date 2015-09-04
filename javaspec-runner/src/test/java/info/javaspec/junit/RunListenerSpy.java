package info.javaspec.junit;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import java.util.function.Consumer;

public final class RunListenerSpy extends RunListener {
  private final Consumer<Event> notifyEvent;

  public RunListenerSpy(Consumer<Event> notifyEvent) {
    this.notifyEvent = notifyEvent;
  }

  @Override
  public void testRunStarted(Description description) throws Exception {
    notifyEvent.accept(Event.describing("testRunStarted", description));
    super.testRunStarted(description);
  }

  @Override
  public void testStarted(Description description) throws Exception {
    notifyEvent.accept(Event.describing("testStarted", description));
    super.testStarted(description);
  }

  @Override
  public void testIgnored(Description description) throws Exception {
    notifyEvent.accept(Event.describing("testIgnored", description));
    super.testIgnored(description);
  }

  @Override
  public void testAssumptionFailure(Failure failure) {
    notifyEvent.accept(Event.failing("testAssumptionFailure", failure));
    super.testAssumptionFailure(failure);
  }

  @Override
  public void testFailure(Failure failure) throws Exception {
    notifyEvent.accept(Event.failing("testFailure", failure));
    super.testFailure(failure);
  }

  @Override
  public void testFinished(Description description) throws Exception {
    notifyEvent.accept(Event.describing("testFinished", description));
    super.testFinished(description);
  }

  @Override
  public void testRunFinished(Result result) throws Exception {
    notifyEvent.accept(Event.named("testRunFinished"));
    super.testRunFinished(result);
  }

  public static final class Event {
    public final String name;
    public final Description description;
    public final Failure failure;

    public static Event named(String name) {
      return new Event(name, null, null);
    }

    public static Event describing(String name, Description description) {
      return new Event(name, description, null);
    }

    public static Event failing(String name, Failure failure) {
      return new Event(name, failure.getDescription(), failure);
    }

    private Event(String name, Description description, Failure failure) {
      this.name = name;
      this.description = description;
      this.failure = failure;
    }

    public String describedClassName() {
      return description == null ? "<no description>" : description.getClassName();
    }

    public String describedMethodName() {
      return description == null ? "<no description>" : description.getMethodName();
    }

    public String getName() { return name; }

    @Override
    public String toString() {
      return String.format("<%s name=%s, description=%s>", getClass().getName(), name, description);
    }
  }
}