package info.javaspec.console;

import info.javaspec.RunObserver;
import info.javaspec.console.Exceptions.InvalidArguments;
import info.javaspec.console.help.HelpObserver;

/** An observer of all things, that handles reporting when running commands. */
public interface Reporter extends HelpObserver, RunObserver {
  void commandFailed(Exception failure);

  void invalidArguments(InvalidArguments failure);
}
