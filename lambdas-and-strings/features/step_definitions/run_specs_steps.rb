## Steps focused on how JavaSpec runs specs

Given("I have a Java class with specs that depend upon external classes") do
  spec_runner_helper.spec_classes = ['info.javaspec.example.rb.HamcrestSpecs']
  spec_runner_helper.add_spec_dependency gradle_cache_path('org.hamcrest/hamcrest-library/1.3/4785a3c21320980282f9f33d0d1264a69040538f/hamcrest-library-1.3.jar')
  spec_runner_helper.add_spec_dependency gradle_cache_path('org.hamcrest/hamcrest-core/1.3/42a25dc3219429f0e5d060061f71acb49bf010a0/hamcrest-core-1.3.jar')

  spec_runner_helper.spec_run_verification do |output|
    expect(output).to match(/Hamcrest/)
    expect(output).to match(/passes/)
  end
end

Given(/^I have a Java class with specs that pass, as well as specs that fail$/) do
  spec_runner_helper.spec_classes = ['info.javaspec.example.rb.OneOfEachResultSpecs']

  spec_runner_helper.spec_run_verification do |output|
    expect(output).to match(/OneOfEachResult/)
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
    expect(output).to match(/Illudium Q-36 Explosive Space Modulator/)
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

  spec_runner_helper.spec_run_verification do |output|
    expect(output).to match(/Spring-operated boxing glove/)
    expect(output).to match(/Tightrope/)
  end
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

def gradle_cache_path(dependency_path)
  # TODO KDK: Using ~/.gradle does not work.  Tab completion would be required to expand ~ in bash.
  # Java could handle this case for the user's own home directory, but not for other users:
  # https://stackoverflow.com/questions/7163364/how-to-handle-in-file-paths
  File.join Dir.home, '.gradle/caches/modules-2/files-2.1', dependency_path
end
