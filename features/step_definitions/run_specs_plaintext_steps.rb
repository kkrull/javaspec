Given(/^I have specs that describe a single subject$/) do
  spec_runner_helper.spec_classes = %w[info.javaspec.example.Tightrope]
end

Given(/^I have specs that describe 2 or more subjects$/) do
  spec_runner_helper.spec_classes = %w[info.javaspec.example.Anvil info.javaspec.example.Tightrope]
end
