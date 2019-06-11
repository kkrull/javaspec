#!/usr/bin/env ruby

text = IO.read 'stacktrace.txt'
puts "Input: #{text.lines.length} lines"

translated = text.lines.map do |line|
  case line 
  when /^\s+at /
    '...stack trace...'
  else
    line
  end
end

puts "Translated: #{translated.length}"
puts translated

trace_shown = false
condensed = translated.select do |line|
  case line
  when /stack trace/
    should_return = not(trace_shown)
    trace_shown = true
    should_return
  else
    true
  end
end

puts "Condensed: #{condensed.length}"
puts condensed

