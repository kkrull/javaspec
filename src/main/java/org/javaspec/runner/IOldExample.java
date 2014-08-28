package org.javaspec.runner;

interface IOldExample {
  String describeSetup();
  String describeAction();
  String describeBehavior();
  String describeCleanup();
  boolean isSkipped();
  void run() throws Exception;
}