require 'stringio'

module Logging
  def logger
    @logger ||= StringIO.new
  end

  def set_logger(string_io)
    @logger = string_io
  end
end

World(Logging)
