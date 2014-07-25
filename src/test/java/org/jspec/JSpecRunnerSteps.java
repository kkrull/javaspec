package org.jspec;

import cucumber.api.PendingException;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public final class JSpecRunnerSteps {
	@Given("^I have a class with JSpec tests in it$")
	public void i_have_a_class_with_JSpec_tests_in_it() throws Throwable {
		throw new PendingException();
	}

	@When("^I run the tests with a JUnit runner$")
	public void i_run_the_tests_with_a_JUnit_runner() throws Throwable {
		throw new PendingException();
	}

	@Then("^the test runner should run all the tests in the class$")
	public void the_test_runner_should_run_all_the_tests_in_the_class() throws Throwable {
		throw new PendingException();
	}
}