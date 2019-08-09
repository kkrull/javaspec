## Steps focused on how JavaSpec runs specs

Given(/^I have a Java class with specs that pass, as well as specs that fail$/) do
  spec_runner_helper.spec_classes = ['info.javaspec.example.rb.OneOfEachResultSpecs']

  spec_runner_helper.spec_run_verification do |output|
    expect(output).to match(/passes/)
    expect(output).to match(/fails/)
  end

  spec_runner_helper.spec_result_verification do |output|
    expect(output).to match(/passes: PASS$/)
    expect(output).to match(/fails: FAIL/)
  end

  spec_runner_helper.spec_error_verification do |output|
    expect(output).to match(/java.lang.AssertionError: bang!/)
  end
end

Given(/^I have a Java class containing specs that describe a subject$/) do
  spec_runner_helper.spec_classes = ['info.javaspec.example.rb.DescribeTwoSpecs']

  spec_runner_helper.spec_run_verification do |output|
    expect(output).to match(/discombobulates/)
    expect(output).to match(/explodes/)
  end
end

Given(/^I have 2 or more Java classes that define lambda specs$/) do
  spec_runner_helper.spec_classes = %w[info.javaspec.example.rb.AllPassSpecs info.javaspec.example.rb.OneFailsSpecs]

  spec_runner_helper.spec_run_verification do |output|
    expect(output).to match(/^AllPass/)
    expect(output).to match(/passes/)

    expect(output).to match(/^OneFails/)
    expect(output).to match(/fails/)

    expect(output).to include('[Testing complete] Passed: 1, Failed: 1, Total: 2')
  end
end

Given(/^I have 2 or more spec collections with a variety of results$/) do
  spec_runner_helper.spec_classes = %w[info.javaspec.example.rb.BeepBeepSpecs]
end

When(/^I ask for help from the run command$/) do
  spec_runner_helper.exec! logger, args: %w[run --help]
end

When(/^I run the specs in those classes$/) do
  spec_runner_helper.exec_run! logger
end

When(/^I run those specs with a plain text reporter$/) do
  spec_runner_helper.exec_run! logger, reporter: 'plaintext'
end

When(/^I run the specs with an incorrect classpath entry for that class$/) do
  spec_runner_helper.exec_run! logger, spec_classpath: File.dirname(__FILE__)
end

Then(/^The runner should describe the subject being tested$/) do
  expect(spec_runner_helper.runner_output).to include('Illudium Q-36 Explosive Space Modulator')
end

Then(/^The runner should describe what went wrong with each failing spec$/) do
  spec_runner_helper.verify_specs_reported_errors
end

Then(/^The runner should run the specs defined in that class$/) do
  spec_runner_helper.verify_specs_ran
end

Then(/^The runner should run the specs in each of those classes$/) do
  spec_runner_helper.verify_specs_ran
end

Then(/^The runner should indicate which specs passed and failed$/) do
  spec_runner_helper.verify_spec_results
end

Then(/^the runner's output should not contain any ANSI escape sequences$/) do
  expect(spec_runner_helper.runner_output).not_to include("\e")
end

Then(/^The runner should list which spec classes could not be loaded$/) do
  expect(spec_runner_helper.runner_output).to include('Failed to load spec class: info.javaspec.example.rb.AllPassSpecs')
end
