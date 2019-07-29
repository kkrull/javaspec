require_relative './helpers/runners/distribution_runner'

Before('@distribution') do
  set_spec_runner_helper DistributionRunner.new
end

Around('@log_commands') do |_scenario, block|
  original_logger = logger

  set_logger ConsoleLogger.new($stdout)
  block.call
  set_logger original_logger
end
