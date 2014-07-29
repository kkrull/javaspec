package org.jspec;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

public final class RunListenerSpy extends RunListener {
  final List<String> notifications;
  
  public RunListenerSpy() {
    this.notifications = new LinkedList<String>();
  }
  
  public RunListenerSpy(List<String> notificationQueue) {
    this.notifications = notificationQueue; //Don't copy; need to update a shared resource
  }
  
  public List<String> notifications() {
    return new ArrayList<String>(notifications);
  }
  
  @Override
  public void testRunStarted(Description description) throws Exception {
//    System.out.printf("testRunStarted: %s\n", description);
    notifications.add("testRunStarted");
    super.testRunStarted(description);
  }

  @Override
  public void testStarted(Description description) throws Exception {
//    System.out.printf("testStarted: %s\n", description);
    notifications.add("testStarted");
    super.testStarted(description);
  }

  @Override
  public void testIgnored(Description description) throws Exception {
//    System.out.printf("testIgnored: %s\n", description);
    notifications.add("testIgnored");
    super.testIgnored(description);
  }
  
  @Override
  public void testAssumptionFailure(Failure failure) {
//    System.out.printf("testAssumptionFailure: %s\n", failure);
    notifications.add("testAssumptionFailure");
    super.testAssumptionFailure(failure);
  }

  @Override
  public void testFailure(Failure failure) throws Exception {
//    System.out.printf("testFailure: %s\n", failure);
    notifications.add("testFailure");
    super.testFailure(failure);
  }

  @Override
  public void testFinished(Description description) throws Exception {
//    System.out.printf("testFinished: %s\n", description);
    notifications.add("testFinished");
    super.testFinished(description);
  }
  
  @Override
  public void testRunFinished(Result result) throws Exception {
//    System.out.printf("testRunFinished: %s\n", result);
    notifications.add("testRunFinished");
    super.testRunFinished(result);
  }
}