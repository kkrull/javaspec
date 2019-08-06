require_relative './helpers/runners/distribution_runner'
require_relative './helpers/runners/java_class_runner'

Before('@distribution_runner') do
  set_spec_runner_helper DistributionRunner.new
end

Before('@java_class_runner') do
  set_spec_runner_helper JavaClassRunner.new
end

Around('@log_commands') do |_scenario, block|
  original_logger = logger

  set_logger ConsoleLogger.new($stdout)
  block.call
  set_logger original_logger
end
