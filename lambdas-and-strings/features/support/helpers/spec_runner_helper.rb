require_relative './runners/distribution_runner'

module SpecRunnerHelper
  def spec_runner_helper
    @spec_runner_helper ||= DistributionRunner.new
  end

  def set_spec_runner_helper(helper)
    @spec_runner_helper = helper
  end
end

World(SpecRunnerHelper)
