require 'rspec/expectations'

RSpec::Matchers.define :visibly_match do |expected_string|
  description do
    'match text (ignoring leading and trailing whitespace)'
  end

  failure_message do |actual_string|
    message = <<~EOF
      expected (raw): #{inspect_lines expected_string.lines}
      got (raw): #{inspect_lines actual_string.lines}

      expected (compared): #{inspect_lines meaningful_lines(expected_string)}
      got (compared): #{inspect_lines meaningful_lines(actual_string)}
    EOF

    message
  end

  match do |actual_string|
    expected_lines = meaningful_lines expected_string
    actual_lines = meaningful_lines actual_string
    actual_lines == expected_lines
  end

  def meaningful_lines(text)
    text.strip.lines.map do |line|
      line.rstrip
    end
  end

  def inspect_lines(lines)
    line_number = 1
    number_of_digits = Math.log10(lines.size) + 1
    output = "#{lines.size} lines\n"
    lines.each do |line|
      output << rjust_number(line_number, number_of_digits)
      output << ": #{format_line line}"
      line_number = line_number + 1
    end

    output
  end

  def format_line(line)
    line_showing_boundaries = "^#{line.sub "\n", ''}"
    line_showing_boundaries + "$\n"
  end

  def rjust_number(number, num_digits)
    "#{number}".rjust num_digits
  end
end
