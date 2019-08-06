require 'aruba/api'

class JavaClassRunner
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

  def exec_help!(logger, command)
    exec! logger, args: ['help', command]
  end

  def exec_run!(logger, reporter: 'plaintext', spec_classpath: spec_class_dir)
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
    verify_class_files_exist
    command = "java -cp #{api_class_dir}:#{jcommander_jar}:#{runner_class_dir} #{runner_class} #{args.join(' ')}"
    logger.command_starting command
    run_simple command, fail_on_error: fail_on_error
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

  def verify_class_files_exist
    expect(runner_class_file).to be_an_existing_file
    spec_classes.each do |class_name|
      expect(spec_class_file(class_name)).to be_an_existing_file
    end
  end

  def api_class_dir
    File.expand_path '../../../../../lambda-api/build/classes/java/main', __FILE__
  end

  def jcommander_jar
    File.join ENV['HOME'], '.gradle/caches/modules-2/files-2.1/com.beust/jcommander/1.74/6b4c0f6e034ee6ce26a72e786cffe6a9c78815d1/jcommander-1.74.jar'
  end

  def runner_class_dir
    File.expand_path '../../../../../console-runner/build/classes/java/main', __FILE__
  end

  def runner_class_file
    path_to_class runner_class, runner_class_dir
  end

  def spec_class_dir
    File.expand_path '../../../../../examples/build/classes/java/main', __FILE__
  end

  def spec_class_file(class_file)
    path_to_class class_file, spec_class_dir
  end

  def path_to_class(class_name, class_path)
    relative_path = "#{class_name.gsub '.', '/'}.class"
    File.expand_path relative_path, class_path
  end
end
