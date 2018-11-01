require 'aruba/api'

module SpecRunnerHelper
  def spec_runner_helper
    @spec_runner_helper ||= SpecRunnerContext.new
  end
end

class SpecRunnerContext
  include Aruba::Api
  attr_accessor :runner_class, :spec_class

  def initialize
    #https://relishapp.com/cucumber/aruba/v/0-11-0/docs/rspec/getting-started-with-rspec-and-aruba#simple-custom-integration
    setup_aruba
  end

  def class_files_should_exist
    expect(runner_class_file).to be_an_existing_file
    expect(spec_class_file).to be_an_existing_file
  end

  def run!(logger)
    command = "java -cp #{runner_class_dir}:#{spec_class_dir} #{runner_class} #{spec_class}"
    logger.puts "Running command: #{command}"
    run_simple command, :fail_on_error => false
  end

  def exit_status
    last_command_stopped.exit_status
  end

  def runner_output
    last_command_stopped.stdout
  end

  private

  def runner_class_dir
    File.expand_path '../../../../console-runner/target/classes', __FILE__
  end

  def runner_class_file
    path_to_class runner_class, runner_class_dir
  end

  def spec_class_dir
    File.expand_path '../../../../console-runner/target/classes', __FILE__
  end

  def spec_class_file
    path_to_class spec_class, spec_class_dir
  end

  def path_to_class(class_name, class_path)
    relative_path = "#{class_name.gsub '.', '/'}.class"
    File.expand_path relative_path, class_path
  end
end

World(SpecRunnerHelper)
