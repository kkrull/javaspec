package org.javaspec.runner;

interface NewExample {
  String getContextName();
  String getName();
  boolean isSkipped();
  void run() throws Exception;
}