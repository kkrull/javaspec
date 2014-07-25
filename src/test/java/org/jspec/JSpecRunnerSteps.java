package org.jspec;

import static org.junit.Assert.*;

import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public final class JSpecRunnerSteps {
  Class<?> testClass;
  RunListenerSpy listener;
  Result result;

  @Given("^I have a class with JSpec tests in it$")
  public void i_have_a_class_with_JSpec_tests_in_it() throws Throwable {
    this.testClass = JSpecTests.class;
  }

  @When("^I run the tests with a JUnit runner$")
  public void i_run_the_tests_with_a_JUnit_runner() throws Throwable {
    JUnitCore junit = new JUnitCore();
    this.listener = new RunListenerSpy();
    junit.addListener(this.listener);
    this.result = junit.run(this.testClass);
  }

  @Then("^the test runner should run all the tests in the class$")
  public void the_test_runner_should_run_all_the_tests_in_the_class() throws Throwable {
    assertEquals(1, this.listener.numTestsStarted);
    assertEquals(1, this.listener.numTestsFinished);
    assertNull(null, this.listener.lastFailure);
  }

  class RunListenerSpy extends RunListener {
    public int numTestsStarted;
    public int numTestsFinished;
    public Failure lastFailure;
    
    @Override
    public void testRunStarted(Description description) throws Exception {
//      System.out.printf("testRunStarted: %s\n", description);
      super.testRunStarted(description);
    }

    @Override
    public void testStarted(Description description) throws Exception {
//      System.out.printf("testStarted: %s\n", description);
      this.numTestsStarted++;
      super.testStarted(description);
    }

    @Override
    public void testIgnored(Description description) throws Exception {
//      System.out.printf("testIgnored: %s\n", description);
      super.testIgnored(description);
    }
    
    @Override
    public void testAssumptionFailure(Failure failure) {
//      System.out.printf("testAssumptionFailure: %s\n", failure);
      super.testAssumptionFailure(failure);
    }

    @Override
    public void testFailure(Failure failure) throws Exception {
//      System.out.printf("testFailure: %s\n", failure);
      this.lastFailure = failure;
      super.testFailure(failure);
    }

    @Override
    public void testFinished(Description description) throws Exception {
//      System.out.printf("testFinished: %s\n", description);
      this.numTestsFinished++;
      super.testFinished(description);
    }
    
    @Override
    public void testRunFinished(Result result) throws Exception {
//      System.out.printf("testRunFinished: %s\n", result);
      super.testRunFinished(result);
    }
  }
}