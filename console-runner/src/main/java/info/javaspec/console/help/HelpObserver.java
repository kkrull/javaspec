package info.javaspec.console.help;

import java.util.List;

public interface HelpObserver {
  void writeMessage(List<String> lines);
}
