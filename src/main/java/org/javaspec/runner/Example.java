package org.javaspec.runner;

interface Example {
  String describeSetup();
  String describeAction();
  String describeBehavior();
  String describeCleanup();
  boolean isSkipped();
  void run() throws Exception;
}