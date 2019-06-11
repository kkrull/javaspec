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
  # Somehow the Gherkin docstring doesn't end in a newline, even though it's there
  expect(spec_runner_helper.runner_output.rstrip).to eq(expected_text)
end

Then(/^the runner's de-tracified output should be$/) do |expected_text|
  newline_gone = spec_runner_helper.runner_output.rstrip

  puts "Input: #{newline_gone.lines.length} lines"
  translated = newline_gone.lines.map do |line|
    case line 
    when /^\s+at /
      "...stack trace...\n"
    else
      line
    end
  end

  puts "Translated: #{translated.length}"
  puts translated

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

  puts "Condensed: #{condensed.length}"
  puts condensed

  expect(condensed).to eq(expected_text.lines)
  expect(condensed.join).to eq(expected_text)
end

Then(/^The runner should indicate that all specs passed$/) do
  expect(spec_runner_helper.exit_status).to eq(0)
  expect(spec_runner_helper.runner_output).to include("[Testing complete] Passed: 1, Failed: 0, Total: 1")
end

Then(/^The runner should indicate that 1 or more specs have failed$/) do
  expect(spec_runner_helper.exit_status).to eq(1)
  expect(spec_runner_helper.runner_output).to include("[Testing complete] Passed: 0, Failed: 1, Total: 1")
end
