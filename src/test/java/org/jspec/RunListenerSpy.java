package org.jspec;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

public final class RunListenerSpy extends RunListener {
  public int numTestsStarted;
  public int numTestsFinished;
  public Failure lastFailure;
  
  @Override
  public void testRunStarted(Description description) throws Exception {
//    System.out.printf("testRunStarted: %s\n", description);
    super.testRunStarted(description);
  }

  @Override
  public void testStarted(Description description) throws Exception {
//    System.out.printf("testStarted: %s\n", description);
    this.numTestsStarted++;
    super.testStarted(description);
  }

  @Override
  public void testIgnored(Description description) throws Exception {
//    System.out.printf("testIgnored: %s\n", description);
    super.testIgnored(description);
  }
  
  @Override
  public void testAssumptionFailure(Failure failure) {
//    System.out.printf("testAssumptionFailure: %s\n", failure);
    super.testAssumptionFailure(failure);
  }

  @Override
  public void testFailure(Failure failure) throws Exception {
//    System.out.printf("testFailure: %s\n", failure);
    this.lastFailure = failure;
    super.testFailure(failure);
  }

  @Override
  public void testFinished(Description description) throws Exception {
//    System.out.printf("testFinished: %s\n", description);
    this.numTestsFinished++;
    super.testFinished(description);
  }
  
  @Override
  public void testRunFinished(Result result) throws Exception {
//    System.out.printf("testRunFinished: %s\n", result);
    super.testRunFinished(result);
  }
}