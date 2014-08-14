package org.jspec.runner;

interface Example {
  String describeBehavior();
  String describeSetup();
  void run() throws Exception;
}