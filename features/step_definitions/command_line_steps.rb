## Steps focused on ConsoleRunner's behavior _as an external process_

Given(/^I have a JavaSpec runner for the console$/) do
  spec_runner_helper.runner_class = 'info.javaspec.console.Main'
end

Given(/^I have a Java class that defines a suite of lambda specs$/) do
  spec_runner_helper.spec_classes = ['info.javaspec.example.OneOfEachResult']

  spec_runner_helper.spec_run_verification do |output|
    expect(output).to match(/^passes/)
    expect(output).to match(/^fails/)
  end

  spec_runner_helper.spec_result_verification do |output|
    expect(output).to match(/^passes: PASS$/)
    expect(output).to match(/^fails: FAIL$/)
  end
end

Given(/^I have a Java class that defines a suite of lambda specs describing a subject$/) do
  spec_runner_helper.spec_classes = ['info.javaspec.example.DescribeTwo']

  spec_runner_helper.spec_run_verification do |output|
    expect(output).to match(/^discombobulates/)
    expect(output).to match(/^explodes/)
  end

  spec_runner_helper.spec_result_verification do |output|
    expect(output).to match(/^discombobulates: PASS$/)
    expect(output).to match(/^explodes: PASS$/)
  end
end

Given(/^I have a Java class that defines a suite of passing lambda specs$/) do
  spec_runner_helper.spec_classes = ['info.javaspec.example.AllPass']
end

Given(/^I have a Java class that defines a suite of 1 or more failing lambda specs$/) do
  spec_runner_helper.spec_classes = ['info.javaspec.example.OneFails']
end

Given(/^I have 1 or more Java classes that defines lambda specs$/) do
  spec_runner_helper.spec_classes = %w[info.javaspec.example.AllPass info.javaspec.example.OneFails]

  spec_runner_helper.spec_run_verification do |output|
    expect(output).to match(/^AllPass/)
    expect(output).to match(/^passes/)

    expect(output).to match(/^OneFails/)
    expect(output).to match(/^fails/)

    expect(output).to include("Passed: 1\tFailed: 1\tTotal: 2")
  end
end

When(/^I run the runner without any arguments$/) do
  spec_runner_helper.verify_class_files_exist
  spec_runner_helper.exec_with_no_command! logger
end

When(/^I run the specs in that class$/) do
  spec_runner_helper.verify_class_files_exist
  spec_runner_helper.exec_run! logger
end

When(/^I run the specs in those classes$/) do
  spec_runner_helper.verify_class_files_exist
  spec_runner_helper.exec_run! logger
end

Then(/^the runner's exit status should be 0$/) do
  expect(spec_runner_helper.exit_status).to eq(0)
end

Then(/^the runner's output should be$/) do |text|
  # Somehow the Gherkin docstring doesn't end in a newline, even though it's there
  expect(spec_runner_helper.runner_output.rstrip).to eq(text)
end

Then(/^The runner should describe what is being tested$/) do
  expect(spec_runner_helper.runner_output).to include('Illudium Q-36 Explosive Space Modulator')
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

Then(/^The runner should indicate that all specs passed$/) do
  expect(spec_runner_helper.exit_status).to eq(0)
  expect(spec_runner_helper.runner_output).to include("Passed: 1\tFailed: 0\tTotal: 1")
end

Then(/^The runner should indicate that 1 or more specs have failed$/) do
  expect(spec_runner_helper.exit_status).to eq(1)
  expect(spec_runner_helper.runner_output).to include("Passed: 0\tFailed: 1\tTotal: 1")
end
