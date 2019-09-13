## Steps focused on JavaSpec's behavior _as an external process_

Given(/^I have a JavaSpec runner for the console$/) do
  spec_runner_helper.runner_class = 'info.javaspec.console.Main'
end

Given(/^I have a Java class that defines a suite of passing lambda specs$/) do
  spec_runner_helper.spec_classes = ['info.javaspec.example.rb.AllPassSpecs']
end

Given(/^I have a Java class that defines a suite of 1 or more failing lambda specs$/) do
  spec_runner_helper.spec_classes = ['info.javaspec.example.rb.OneFailsSpecs']
end

When(/^I ask for help on the run command$/) do
  spec_runner_helper.exec_help! logger, 'run'
end

When(/^I run the runner without any arguments$/) do
  spec_runner_helper.exec! logger
end

When(/^I run the specs in that class$/) do
  spec_runner_helper.exec_run! logger
end

Then(/^the runner's exit status should be 0$/) do
  expect(spec_runner_helper.exit_status).to eq(0)
end

Then(/^the runner's output should be$/) do |expected_text|
  # expect_text_like_docstring expected_text, spec_runner_helper.runner_output
  expect_text expected_text, spec_runner_helper.runner_output
end

Then(/^the runner's de-tracified output should be$/) do |expected_text|
  stacktrace_collapsed = collapse_stacktrace spec_runner_helper.runner_output
  expect_text_like_docstring expected_text, stacktrace_collapsed
end

Then(/^The runner should indicate that all specs passed$/) do
  expect(spec_runner_helper.runner_output).to include("[Testing complete] Passed: 1, Failed: 0, Total: 1")
  expect(spec_runner_helper.exit_status).to eq(0)
end

Then(/^The runner should indicate that 1 or more specs have failed$/) do
  expect(spec_runner_helper.runner_output).to include("[Testing complete] Passed: 0, Failed: 1, Total: 1")
  expect(spec_runner_helper.exit_status).to eq(1)
end

Then(/^The runner should indicate that running specs failed$/) do
  expect(spec_runner_helper.exit_status).to be > 0
end

def collapse_stacktrace(output)
  translated = output.lines.map do |line|
    case line
    when /^\s+at /
      "...stack trace...\n"
    else
      line
    end
  end

  trace_shown = false
  condensed = translated.select do |line|
    case line
    when /stack trace/
      should_return = not(trace_shown)
      trace_shown = true
      should_return
    else
      true
    end
  end

  condensed.join
end

def expect_text_like_docstring(expected_text, actual_text)
  # Somehow the Gherkin docstring doesn't end in a newline, even though it's there
  expect(actual_text).to eq(expected_text + "\n")
end

def expect_text(expected_raw, actual_raw)
  expected_lines = expected_raw.lines.map do |line|
    line.rstrip
  end

  actual_lines = actual_raw.lines.map do |line|
    line.rstrip
  end

  #TODO KDK: Get it to show a formatted listing of the expected/actual lines
  # Isn't there a DocString#diff! ?
  expect(actual_lines).to eq(expected_lines)
end
