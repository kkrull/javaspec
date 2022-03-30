package info.javaspec.console;

import com.beust.jcommander.JCommander;
import info.javaspec.RunObserver;
import info.javaspec.console.help.HelpObserver;

import java.net.URL;
import java.util.List;

public interface CommandFactory {
  Command helpCommand(HelpObserver observer, JCommander jCommander);

  Command runSpecsCommand(RunObserver observer, List<URL> specClassPath, List<String> classNames);
}
