## Steps focused on ConsoleRunner's behavior _as a process_

Given("I have a Java class that contains 1 or more specs") do
  @runner_class_dir = File.expand_path '../../../console-runner/target/classes', __FILE__
  @spec_class_name = 'info.javaspec.example.PassIt'
end

When("I run the specs in that class") do
  runner_class_name = 'info.javaspec.console.Runner'
  run_simple "java -cp #{@runner_class_dir} #{runner_class_name} #{@spec_class_name}"
end

Then("The runner should run the specs defined in that class") do
  expect(last_command_stopped.stdout).to match(/^Greeter/)
  expect(last_command_stopped.stdout).to match(/says hello/)
end

Then("The runner should indicate whether each spec passed or failed") do
  expect(last_command_stopped.stdout).to match(/says hello: PASS/)
end

Then("The runner should indicate whether all specs passed, or any failed") do
  expect(last_command_stopped.stdout).to include("Passed: 1\tFailed: 0\tTotal: 1")
  expect(last_command_stopped.exit_status).to eq(0)
end
