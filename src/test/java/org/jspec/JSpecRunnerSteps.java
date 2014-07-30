package org.jspec;

import java.util.LinkedList;
import java.util.List;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;

public final class JSpecRunnerSteps {
  final List<String> events = new LinkedList<String> ();
  Class<?> testClass;
  Result result;

  @Given("^I have a class with JSpec tests in it$")
  public void i_have_a_class_with_JSpec_tests_in_it() throws Throwable {
    JSpecTests.One.notifyEvent = events::add;
    this.testClass = JSpecTests.One.class;
  }

  @When("^I run the tests with a JUnit runner$")
  public void i_run_the_tests_with_a_JUnit_runner() throws Throwable {
    JUnitCore junit = new JUnitCore();
    junit.addListener(new RunListenerSpy(events::add));
    this.result = junit.run(testClass);
  }

  @Then("^the test runner should run all the tests in the class$")
  public void the_test_runner_should_run_all_the_tests_in_the_class() throws Throwable {
    assertThat(
      String.format("\nActual: %s", events),
      events, 
      hasItems("JSpecTests.One::only_test"));
  }
}