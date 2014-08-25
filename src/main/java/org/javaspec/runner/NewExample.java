package org.javaspec.runner;

interface NewExample {
//  String describeSetup();
//  String describeAction();
  String describeBehavior();
//  String describeCleanup();
  String getContextName();
  boolean isSkipped();
  void run() throws Exception;
}