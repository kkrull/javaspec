## Steps focused on ConsoleRunner's behavior _as a process_

Given("I have a JavaSpec runner for the console") do
  java_helper.runner_class_name = 'info.javaspec.console.Runner'
end

Given("I have a Java class that defines a suite of lambda specs") do
  java_helper.spec_class_name = 'info.javaspec.example.OneFails'
end

Given("I have a Java class that defines a suite of passing lambda specs") do
  java_helper.spec_class_name = 'info.javaspec.example.AllPass'
end

Given("I have a Java class that defines a suite of 1 or more failing lambda specs") do
  java_helper.spec_class_name = 'info.javaspec.example.OneFails'
end

When("I run the specs in that class") do
  expect(java_helper.runner_class_file).to be_an_existing_file
  expect(java_helper.spec_class_file).to be_an_existing_file
  command = "java -cp #{runner_class_dir}:#{spec_class_dir} #{java_helper.runner_class_name} #{java_helper.spec_class_name}"

  logger.puts "Running command: #{command}"
  run_simple command, :fail_on_error => false
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

Then("The runner should indicate which specs passed and failed") do
  pending # Write code here that turns the phrase above into concrete actions
end

Then("The runner should indicate that all specs passed") do
  expect(last_command_stopped.exit_status).to eq(0)
  expect(last_command_stopped.stdout).to include("Passed: 1\tFailed: 0\tTotal: 1")
end

Then("The runner should indicate that 1 or more specs have failed") do
  expect(last_command_stopped.exit_status).to eq(1)
  expect(last_command_stopped.stdout).to include("Passed: 0\tFailed: 1\tTotal: 1")
end

def runner_class_dir
  File.expand_path '../../../console-runner/target/classes', __FILE__
end

def spec_class_dir
  File.expand_path '../../../console-runner/target/classes', __FILE__
end
