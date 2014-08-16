package org.javaspec.runner;

interface Example {
  String describeSetup();
  String describeAction();
  String describeBehavior();
  String describeCleanup();
  void run() throws Exception;
}