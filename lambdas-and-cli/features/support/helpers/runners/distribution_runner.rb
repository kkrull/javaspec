require 'aruba/api'

class DistributionRunner
  include Aruba::Api
  attr_accessor :runner_class, :spec_classes, :spec_classpath

  def initialize
    #https://relishapp.com/cucumber/aruba/v/0-11-0/docs/rspec/getting-started-with-rspec-and-aruba#simple-custom-integration
    setup_aruba
    self.spec_classes = []
    self.spec_classpath = []
  end

  def add_spec_dependency(classpath_entry)
    @spec_classpath << classpath_entry
  end

  def exec_help!(logger, command)
    exec! logger, args: ['help', command]
  end

  def exec_run!(logger, reporter: 'plaintext', spec_classpath: default_spec_classpath)
    exec! logger,
      args: [
        'run',
        "--reporter=#{reporter}",
        "--spec-classpath=#{spec_classpath}",
        *spec_classes
      ],
      fail_on_error: false
  end

  def exec!(logger, args: [], fail_on_error: false)
    verify_distribution_exists
    command = "#{start_script_file} #{args.join(' ')}"
    logger.command_starting command
    run_simple command, fail_on_error: fail_on_error
  end

  def exit_status
    last_command_stopped.exit_status
  end

  def runner_output
    last_command_stopped.output
  end

  def spec_error_verification(&block)
    @error_assertion = block
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

  def verify_specs_reported_errors
    @error_assertion.call runner_output
  end

  def verify_spec_results
    @result_assertion.call runner_output
  end

  private

  def verify_distribution_exists
    expect(start_script_file).to be_an_existing_file
  end

  def default_spec_classpath
    classpath = [spec_class_dir] + @spec_classpath
    classpath.join(File::PATH_SEPARATOR)
  end

  def spec_class_dir
    File.expand_path '../../../../../examples/build/classes/java/main', __FILE__
  end

  def start_script_file
    File.expand_path '../../../../../console-runner/build/install/javaspec/bin/javaspec', __FILE__
  end
end
