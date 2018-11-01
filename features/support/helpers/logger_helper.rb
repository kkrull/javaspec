require 'stringio'

module Logging
  def logger
    @logger ||= ConsoleLogger.new(StringIO.new)
  end

  def set_logger(string_io)
    @logger = string_io
  end
end

class ConsoleLogger
  attr_reader :string_io

  def initialize(string_io)
    @string_io = string_io
  end

  def command_starting(command_line)
    string_io.puts "Running command: #{command_line}"
  end
end

World(Logging)
