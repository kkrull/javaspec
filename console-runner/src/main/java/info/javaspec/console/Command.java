package info.javaspec.console;

import info.javaspec.RunObserver;

@FunctionalInterface
interface Command {
  int run(RunObserver observer);
}
