# require 'aruba/rspec'
#TODO KDK: include Aruba::Api in JavaContext

module JavaHelper
  def java_helper
    @java_helper ||= JavaContext.new
  end
end

class JavaContext
  attr_accessor :runner_class_name, :spec_class_name

  def runner_class_file
    path_to_class runner_class_name, runner_class_dir
  end

  def spec_class_file
    path_to_class spec_class_name, spec_class_dir
  end

  def runner_class_dir
    File.expand_path '../../../../console-runner/target/classes', __FILE__
  end

  def spec_class_dir
    File.expand_path '../../../../console-runner/target/classes', __FILE__
  end

  private

  def path_to_class(class_name, class_path)
    relative_path = "#{class_name.gsub '.', '/'}.class"
    File.expand_path relative_path, class_path
  end
end

World(JavaHelper)
