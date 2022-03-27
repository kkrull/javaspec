Given(/^I have specs that describe a single subject$/) do
  spec_runner_helper.spec_classes = %w[info.javaspec.example.rb.TightropeSpecs]
end

Given(/^I have specs that describe 2 or more subjects$/) do
  spec_runner_helper.spec_classes = %w[info.javaspec.example.rb.AnvilSpecs info.javaspec.example.rb.TightropeSpecs]
end

Given(/^I have specs that describe context-specific behavior$/) do
  spec_runner_helper.spec_classes = %w[info.javaspec.example.rb.SpringOperatedBoxingGloveSpecs]
end

Given(/^I have specs where 1 or more of them fail$/) do
  spec_runner_helper.spec_classes = %w[info.javaspec.example.rb.CoyoteAnvilSpecs]
end
