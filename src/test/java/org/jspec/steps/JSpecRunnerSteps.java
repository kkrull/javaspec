package org.jspec.steps;

import static java.util.Collections.synchronizedList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import org.jspec.proto.JSpecExamples;
import org.jspec.proto.RunWithJSpecRunner;
import org.jspec.runner.Runners;
import org.jspec.util.RunListenerSpy.Event;
import org.junit.runner.JUnitCore;

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
    JSpecExamples.FullFixture.setEventListener(events::add);
  }
  
  @After
  public void recallSpies() {
    JSpecExamples.One.setEventListener(null);
    RunWithJSpecRunner.setEventListener(null);
    JSpecExamples.FullFixture.setEventListener(null);
  }

  @Given("^I have a class with JSpec tests in it$")
  public void i_have_a_class_with_JSpec_tests_in_it() throws Throwable {
    this.testClass = JSpecExamples.One.class;
  }
  
  @When("^I run the tests with a JSpec runner$")
  public void i_run_the_tests_with_a_JSpec_runner() throws Throwable {
    Runners.runAll(Runners.of(testClass), notifyEventName);
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
  
  @Given("^I have JSpec test with test fixture functions$")
  public void i_have_JSpec_test_with_test_fixture_functions() throws Throwable {
    this.testClass = JSpecExamples.FullFixture.class;
  }

  @When("^I run the test$")
  public void i_run_the_test() throws Throwable {
    Runners.runAll(Runners.of(testClass), notifyEventName);
  }
  
  @Then("^the test runner should run the test within the context of the test fixture$")
  public void the_test_runner_should_run_the_test_within_the_context_of_the_test_fixture() throws Throwable {
    assertThat(String.format("\nActual: %s", events), executedMethods(), hasSize(3));
  }

  @Then("^the test runner should run the Establish function first,.*$")
  public void the_test_runner_should_run_the_Establish_function_first() throws Throwable {
    assertThat(String.format("\nActual: %s", events),
      executedMethods().get(0), equalTo("JSpecExamples.FullFixture::arrange"));
  }

  @Then("^the test runner should run the Because function second,.*$")
  public void the_test_runner_should_run_the_Because_function_second() throws Throwable {
    assertThat(String.format("\nActual: %s", events),
      executedMethods().get(1), equalTo("JSpecExamples.FullFixture::act"));
  }

  @Then("^the test runner should run the It function third,.*$")
  public void the_test_runner_should_run_the_It_function_third() throws Throwable {
    assertThat(String.format("\nActual: %s", events),
      executedMethods().get(2), equalTo("JSpecExamples.FullFixture::assert"));
  }

  @Then("^the test runner should run the Cleanup function fourth,.*$")
  public void the_test_runner_should_run_the_Cleanup_function_fourth() throws Throwable {
    assertThat(String.format("\nActual: %s", events),
      executedMethods().get(42), equalTo("JSpecExamples.FullFixture::cleanup"));
  }
  
  private List<String> executedMethods() {
    return events.stream().filter(x -> !x.startsWith("test")).collect(toList());
  }
}