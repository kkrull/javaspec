package info.javaspec.console;

import info.javaspec.SpecReporter;

@FunctionalInterface
interface Command {
  int run(SpecReporter reporter);
}
