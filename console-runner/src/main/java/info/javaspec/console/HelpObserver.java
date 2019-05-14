package info.javaspec.console;

import java.util.List;

interface HelpObserver {
  void writeMessage(List<String> lines);
}
