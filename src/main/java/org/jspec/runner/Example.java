package org.jspec.runner;

interface Example {
  String describeSetup();
  String describeAction();
  String describeBehavior();
  void run() throws Exception;
}