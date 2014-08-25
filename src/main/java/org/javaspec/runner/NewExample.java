package org.javaspec.runner;

interface NewExample {
  String describeBehavior();
  String getContextName();
  boolean isSkipped();
  void run() throws Exception;
}