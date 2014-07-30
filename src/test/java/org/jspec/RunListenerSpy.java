package org.jspec;

import java.util.function.Consumer;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

public final class RunListenerSpy extends RunListener {
  final Consumer<String> notifyEvent;
  
  public RunListenerSpy(Consumer<String> notifyEvent) {
    this.notifyEvent = notifyEvent;
  }
  
  @Override
  public void testRunStarted(Description description) throws Exception {
    notifyEvent.accept("testRunStarted");
    super.testRunStarted(description);
  }

  @Override
  public void testStarted(Description description) throws Exception {
    notifyEvent.accept("testStarted");
    super.testStarted(description);
  }

  @Override
  public void testIgnored(Description description) throws Exception {
    notifyEvent.accept("testIgnored");
    super.testIgnored(description);
  }
  
  @Override
  public void testAssumptionFailure(Failure failure) {
    notifyEvent.accept("testAssumptionFailure");
    super.testAssumptionFailure(failure);
  }

  @Override
  public void testFailure(Failure failure) throws Exception {
    notifyEvent.accept("testFailure");
    super.testFailure(failure);
  }

  @Override
  public void testFinished(Description description) throws Exception {
    notifyEvent.accept("testFinished");
    super.testFinished(description);
  }
  
  @Override
  public void testRunFinished(Result result) throws Exception {
    notifyEvent.accept("testRunFinished");
    super.testRunFinished(result);
  }
}