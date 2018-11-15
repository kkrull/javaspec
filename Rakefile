require 'cucumber'
require 'cucumber/rake/task'


desc 'Compile and run all tests'
task default: %w[java:test cucumber]

Cucumber::Rake::Task.new

namespace :cucumber do
  desc 'Run Yard server that auto-generates documentation for all gems'
  task 'doc-server' do
    sh *%w[yard server --gems]
  end

  desc 'Run Cucumber scenarios tagged with @focus'
  task :focus do
    sh *%w[bundle exec cucumber -t @focus]
  end
end

namespace :java do
  desc 'Set a new version for all artifacts'
  task 'bump-version' do
    sh *%w[mvn versions:set]
    sh *%w[mvn versions:commit]
    puts "Remember to update the version expectation in the cucumber-jvm tests"
  end

  desc 'Remove everything generated and compiled from Java code'
  task :clean do
    sh *%w[mvn clean]
  end

  desc 'Compile Java sources'
  task :compile do
    sh *%w[mvn compile]
  end

  desc 'Test with JUnit'
  task :junit do
    sh *%w[mvn test]
  end

  desc 'Run all tests on Java code'
  task :test do
    #Cucumber options: https://github.com/cucumber/cucumber-java-skeleton#overriding-options
    sh *%w[mvn verify]
  end
end

namespace :release do
  desc 'Build artifacts for release'
  task :build do
    sh *%w[mvn -Pgpg,release clean install]
  end

  desc 'Deploy a release'
  task :deploy do
    sh *%w[mvn -Pgpg,release deploy]
  end
end
