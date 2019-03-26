require 'aruba/api'

module SpecRunnerHelper
  def spec_runner_helper
    @spec_runner_helper ||= SpecRunnerContext.new
  end
end

class SpecRunnerContext
  include Aruba::Api
  attr_accessor :runner_class, :spec_classes

  def initialize
    #https://relishapp.com/cucumber/aruba/v/0-11-0/docs/rspec/getting-started-with-rspec-and-aruba#simple-custom-integration
    setup_aruba
    self.spec_classes = []
  end

  def exit_status
    last_command_stopped.exit_status
  end

  def exec_run!(logger)
    verify_class_files_exist
    command = "java -cp #{runner_class_dir}:#{spec_class_dir} #{runner_class} run #{spec_classes.join(' ')}"
    logger.command_starting command
    run_simple command, :fail_on_error => false
  end

  def exec_with_no_command!(logger)
    verify_class_files_exist
    command = "java -cp #{runner_class_dir}:#{spec_class_dir} #{runner_class}"
    logger.command_starting command
    run_simple command, :fail_on_error => false
  end

  def runner_output
    last_command_stopped.stdout
  end

  def spec_result_verification(&block)
    @result_assertion = block
  end

  def spec_run_verification(&block)
    @run_assertion = block
  end

  def verify_specs_ran
    @run_assertion.call runner_output
  end

  def verify_spec_results
    @result_assertion.call runner_output
  end

  private

  def verify_class_files_exist
    expect(runner_class_file).to be_an_existing_file
    spec_classes.each do |class_name|
      expect(spec_class_file(class_name)).to be_an_existing_file
    end
  end

  def runner_class_dir
    File.expand_path '../../../../console-runner/target/classes', __FILE__
  end

  def runner_class_file
    path_to_class runner_class, runner_class_dir
  end

  def spec_class_dir
    File.expand_path '../../../../console-runner/target/classes', __FILE__
  end

  def spec_class_file(class_file)
    path_to_class class_file, spec_class_dir
  end

  def path_to_class(class_name, class_path)
    relative_path = "#{class_name.gsub '.', '/'}.class"
    File.expand_path relative_path, class_path
  end
end

World(SpecRunnerHelper)
