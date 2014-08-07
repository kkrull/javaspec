package org.jspec.util;

import java.util.function.Consumer;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

public final class RunListenerSpy extends RunListener {
  private final Consumer<Event> notifyEvent;
  
  public RunListenerSpy(Consumer<Event> notifyEvent) {
    this.notifyEvent = notifyEvent;
  }
  
  @Override
  public void testRunStarted(Description description) throws Exception {
    notifyEvent.accept(new Event("testRunStarted", description));
    super.testRunStarted(description);
  }

  @Override
  public void testStarted(Description description) throws Exception {
    notifyEvent.accept(new Event("testStarted", description));
    super.testStarted(description);
  }

  @Override
  public void testIgnored(Description description) throws Exception {
    notifyEvent.accept(new Event("testIgnored", description));
    super.testIgnored(description);
  }
  
  @Override
  public void testAssumptionFailure(Failure failure) {
    notifyEvent.accept(new Event("testAssumptionFailure", null));
    super.testAssumptionFailure(failure);
  }

  @Override
  public void testFailure(Failure failure) throws Exception {
    notifyEvent.accept(new Event("testFailure", null));
    super.testFailure(failure);
  }

  @Override
  public void testFinished(Description description) throws Exception {
    notifyEvent.accept(new Event("testFinished", description));
    super.testFinished(description);
  }
  
  @Override
  public void testRunFinished(Result result) throws Exception {
    notifyEvent.accept(new Event("testRunFinished", null));
    super.testRunFinished(result);
  }
  
  public static class Event {
    public final String name;
    public final Description description;
    
    public Event(String name, Description description) {
      this.name = name;
      this.description = description;
    }
  }
}