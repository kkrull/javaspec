package org.javaspec.steps;

import static java.util.Collections.synchronizedList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import org.javaspec.proto.ContextClasses;
import org.javaspec.proto.RunWithJavaSpecRunner;
import org.javaspec.runner.Runners;
import org.javaspec.util.RunListenerSpy.Event;
import org.junit.runner.JUnitCore;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public final class JavaSpecRunnerSteps {
  private final List<String> events = synchronizedList(new LinkedList<String> ()); //In case JUnit uses threads per test
  private final Consumer<Event> notifyEventName = x -> events.add(x.name);
  private Class<?> testClass;
  
  @Before
  public void setupTestExecutionSpy() {
    ContextClasses.FullFixture.setEventListener(events::add);
    ContextClasses.OneIt.setEventListener(events::add);
    ContextClasses.TwoIt.setEventListener(events::add);
    RunWithJavaSpecRunner.setEventListener(events::add);
  }
  
  @After
  public void recallSpies() {
    ContextClasses.FullFixture.setEventListener(null);
    ContextClasses.OneIt.setEventListener(null);
    ContextClasses.TwoIt.setEventListener(null);
    RunWithJavaSpecRunner.setEventListener(null);
  }

  /* Given */
  
  @Given("^I have a class with JavaSpec tests in it$")
  public void i_have_a_class_with_JavaSpec_tests_in_it() throws Throwable {
    this.testClass = ContextClasses.OneIt.class;
  }
  
  @Given("^I have a class with JavaSpec tests in it that is marked to run with a JavaSpec runner$")
  public void i_have_a_class_with_JavaSpec_tests_in_it_that_is_marked_to_run_with_a_JavaSpec_runner() throws Throwable {
    this.testClass = RunWithJavaSpecRunner.class;
  }
  
  @Given("^I have a JavaSpec test with test fixture lambdas$")
  public void i_have_a_JavaSpec_test_with_test_fixture_lambdas() throws Throwable {
    this.testClass = ContextClasses.FullFixture.class;
  }

  @Given("^I have a JavaSpec test with 1 or more It fields that are assigned to no-argument lambdas$")
  public void i_have_a_JavaSpec_test_with_It_fields_that_are_assigned_to_no_argument_lambdas() throws Throwable {
    this.testClass = ContextClasses.TwoIt.class;
  }

  @Given("^I have a JavaSpec test with a blank It field$")
  public void i_have_a_JavaSpec_test_with_a_blank_It_field() throws Throwable {
    this.testClass = ContextClasses.PendingIt.class;
  }
  
  /* When */
  
  @When("^I run the tests?$")
  public void i_run_the_test() throws Throwable {
    Runners.runAll(Runners.of(testClass), notifyEventName);
  }
  
  @When("^I run the tests with a JavaSpec runner$")
  public void i_run_the_tests_with_a_JavaSpec_runner() throws Throwable {
    Runners.runAll(Runners.of(testClass), notifyEventName);
  }

  @When("^I run the tests with a JUnit runner$")
  public void i_run_the_tests_with_a_JUnit_runner() throws Throwable {
    JUnitCore.runClasses(testClass);
  }
  
  /* Then */
  
  @Then("^the test runner should run all the tests in the class$")
  public void the_test_runner_should_run_all_the_tests_in_the_class() throws Throwable {
    assertThat(describeEvents(), executedMethods(), hasItems("ContextClasses.OneIt::only_test"));
  }

  @Then("^the test runner should run all the tests in the marked class$")
  public void the_test_runner_should_run_all_the_tests_in_the_marked_class() throws Throwable {
    assertThat(describeEvents(), executedMethods(), hasItems("RunWithJavaSpecRunner::only_test"));
  }
  
  @Then("^the test runner should run one test for every It field$")
  public void the_test_runner_should_run_one_test_for_every_It_field() throws Throwable {
    assertThat(describeEvents(), executedMethods(), contains("TwoIt::first_test", "TwoIt::second_test"));
  }
  
  @Then("^the test runner should run the test within the context of the test fixture$")
  public void the_test_runner_should_run_the_test_within_the_context_of_the_test_fixture() throws Throwable {
    assertThat(describeEvents(), executedMethods(), hasSize(5));
  }

  @Then("^the test runner should run the Establish lambda first.*$")
  public void the_test_runner_should_run_the_Establish_lambda_first() throws Throwable {
    assertThat(describeEvents(), executedMethods().get(1), equalTo("ContextClasses.FullFixture::arrange"));
  }

  @Then("^the test runner should run the Because lambda second.*$")
  public void the_test_runner_should_run_the_Because_lambda_second() throws Throwable {
    assertThat(describeEvents(), executedMethods().get(2), equalTo("ContextClasses.FullFixture::act"));
  }

  @Then("^the test runner should run the It lambda third.*$")
  public void the_test_runner_should_run_the_It_lambda_third() throws Throwable {
    assertThat(describeEvents(), executedMethods().get(3), equalTo("ContextClasses.FullFixture::assert"));
  }

  @Then("^the test runner should run the Cleanup lambda fourth.*$")
  public void the_test_runner_should_run_the_Cleanup_lambda_fourth() throws Throwable {
    assertThat(describeEvents(), executedMethods().get(4), equalTo("ContextClasses.FullFixture::cleans"));
  }

  @Then("^the test runner should ignore the test$")
  public void the_test_runner_should_ignore_the_test() throws Throwable {
    assertThat(describeEvents(), testNotifications(), contains("testIgnored"));
  }
  
  /* Helpers */

  private String describeEvents() {
    return String.format("\nActual: %s", events);
  }
  
  private List<String> executedMethods() {
    return events.stream().filter(x -> !x.startsWith("test")).collect(toList());
  }
  
  private List<String> testNotifications() {
    return events.stream().filter(x -> x.startsWith("test")).collect(toList());
  }
}