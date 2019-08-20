package info.javaspec.console;

import info.javaspec.RunObserver;
import info.javaspec.console.help.HelpObserver;

import java.net.URL;
import java.util.List;

public interface CommandFactory {
  Command helpCommand(HelpObserver observer);

  Command helpCommand(HelpObserver observer, String forCommandNamed);

  Command runSpecsCommand(RunObserver observer, URL specClassPath, List<String> classNames);
}
