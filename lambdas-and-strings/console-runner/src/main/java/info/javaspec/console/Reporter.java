package info.javaspec.console;

import info.javaspec.RunObserver;
import info.javaspec.console.help.HelpObserver;

/** An observer of all things, that handles reporting when running commands. */
public interface Reporter extends HelpObserver, RunObserver {
  void commandFailed(Exception failure);
}
