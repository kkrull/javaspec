require 'cucumber'
require 'cucumber/rake/task'

console_runner_class_dir = File.join File.dirname(__FILE__), 'console-runner/target/classes'
features_dir = File.join File.dirname(__FILE__), 'features'

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


namespace 'cucumber-docker' do
  desc 'Build the image used to run Cucumber tests'
  task :build do
    sh *%w[docker build -t javaspec/cucumber-tests .]
  end

  desc 'Run an interactive session in the Cucumber container'
  task :interactive do
    cmd = [
      'docker run',
      '--entrypoint bash',
      '--rm',
      '-it',
      "-v #{console_runner_class_dir}:/usr/src/app/console-runner/target/classes",
      "-v #{features_dir}:/usr/src/app/features",
      'javaspec/cucumber-tests'
    ].join(' ')
    sh cmd
  end

  desc 'Run Cucumber scenarios in a Docker container'
  task :run do
    cmd = [
      'docker run',
      '--rm',
      "-v #{console_runner_class_dir}:/usr/src/app/console-runner/target/classes",
      "-v #{features_dir}:/usr/src/app/features",
      'javaspec/cucumber-tests'
    ].join(' ')
    sh cmd
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
