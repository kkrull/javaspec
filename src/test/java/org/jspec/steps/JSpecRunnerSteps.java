package org.jspec.steps;

import static java.util.Collections.synchronizedList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;

import java.util.LinkedList;
import java.util.List;

import org.jspec.JSpecRunner;
import org.jspec.JSpecTests;
import org.jspec.RunWithJSpecRunner;
import org.jspec.util.RunListenerSpy;
import org.junit.runner.JUnitCore;
import org.junit.runner.notification.RunNotifier;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public final class JSpecRunnerSteps {
  final List<String> events = synchronizedList(new LinkedList<String> ()); //In case JUnit uses threads per test
  Class<?> testClass;
  
  @Before
  public void setupTestExecutionSpy() {
    JSpecTests.One.setEventListener(events::add);
    RunWithJSpecRunner.setEventListener(events::add);
  }
  
  @After
  public void recallSpies() {
    JSpecTests.One.setEventListener(null);
    RunWithJSpecRunner.setEventListener(null);
  }

  @Given("^I have a class with JSpec tests in it$")
  public void i_have_a_class_with_JSpec_tests_in_it() throws Throwable {
    this.testClass = JSpecTests.One.class;
  }
  
  @When("^I run the tests with a JSpec runner$")
  public void i_run_the_tests_with_a_JSpec_runner() throws Throwable {
    RunNotifier notifier = new RunNotifier();
    notifier.addListener(new RunListenerSpy(events::add));
    JSpecRunner runner = new JSpecRunner(testClass);
    runner.run(notifier);
  }

  @Then("^the test runner should run all the tests in the class$")
  public void the_test_runner_should_run_all_the_tests_in_the_class() throws Throwable {
    assertThat(
      String.format("\nActual: %s", events),
      events, 
      hasItems("JSpecTests.One::only_test"));
  }
  
  @Given("^I have a class with JSpec tests in it that is marked to run with a JSpec runner$")
  public void i_have_a_class_with_JSpec_tests_in_it_that_is_marked_to_run_with_a_JSpec_runner() throws Throwable {
    this.testClass = RunWithJSpecRunner.class;
  }
  
  @When("^I run the tests with a JUnit runner$")
  public void i_run_the_tests_with_a_JUnit_runner() throws Throwable {
    JUnitCore.runClasses(testClass);
  }
  
  @Then("^the test runner should run all the tests in the marked class$")
  public void the_test_runner_should_run_all_the_tests_in_the_marked_class() throws Throwable {
    assertThat(
      String.format("\nActual: %s", events),
      events,
      hasItems("RunWithJSpecRunner::only_test"));
  }
}