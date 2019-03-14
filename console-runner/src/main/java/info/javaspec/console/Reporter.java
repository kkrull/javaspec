package info.javaspec.console;

import info.javaspec.RunObserver;
import info.javaspec.console.HelpCommand.HelpObserver;

public interface Reporter extends HelpObserver, RunObserver { }
