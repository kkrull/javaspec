package info.javaspec.console;

import info.javaspec.RunObserver;

/** An observer of all things, that handles reporting when running commands. */
public interface Reporter extends HelpObserver, RunObserver { }
