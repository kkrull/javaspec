package org.jspec.steps;

import static java.util.Collections.synchronizedList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasItems;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import org.jspec.proto.JSpecExamples;
import org.jspec.proto.RunWithJSpecRunner;
import org.jspec.runner.JSpecRunner;
import org.jspec.util.RunListenerSpy;
import org.jspec.util.RunListenerSpy.Event;
import org.junit.runner.JUnitCore;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public final class JSpecRunnerSteps {
  private final List<String> events = synchronizedList(new LinkedList<String> ()); //In case JUnit uses threads per test
  private final Consumer<Event> notifyEventName = x -> events.add(x.name);
  private Class<?> testClass;
  
  @Before
  public void setupTestExecutionSpy() {
    JSpecExamples.One.setEventListener(events::add);
    RunWithJSpecRunner.setEventListener(events::add);
  }
  
  @After
  public void recallSpies() {
    JSpecExamples.One.setEventListener(null);
    RunWithJSpecRunner.setEventListener(null);
  }

  @Given("^I have a class with JSpec tests in it$")
  public void i_have_a_class_with_JSpec_tests_in_it() throws Throwable {
    this.testClass = JSpecExamples.One.class;
  }
  
  @When("^I run the tests with a JSpec runner$")
  public void i_run_the_tests_with_a_JSpec_runner() throws Throwable {
    runWithJSpecRunner();
  }

  @Then("^the test runner should run all the tests in the class$")
  public void the_test_runner_should_run_all_the_tests_in_the_class() throws Throwable {
    assertThat(String.format("\nActual: %s", events),
      events, hasItems("JSpecExamples.One::only_test"));
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
    assertThat(String.format("\nActual: %s", events),
      events, hasItems("RunWithJSpecRunner::only_test"));
  }
  
  @Given("^I have JSpec tests with an Establish block$")
  public void i_have_JSpec_tests_with_an_Establish_block() throws Throwable {
    this.testClass = JSpecExamples.EstablishTest.class;
  }
  
  @When("^I run the tests$")
  public void i_run_the_tests() throws Throwable {
    runWithJSpecRunner();
  }

  @Then("^the test runner should run the Establish block before each test$")
  public void the_test_runner_should_run_the_Establish_block_before_each_test() throws Throwable {
    assertThat(String.format("\nActual: %s", events),
      events, contains("testStarted", "testFinished"));
  }

  private void runWithJSpecRunner() throws InitializationError {
    RunNotifier notifier = new RunNotifier();
    notifier.addListener(new RunListenerSpy(notifyEventName));
    Runner runner = new JSpecRunner(testClass);
    runner.run(notifier);
  }
}