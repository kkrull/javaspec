package org.javaspec.runner;

interface Example {
  String getContextName();
  String getName();
  boolean isSkipped();
  void run() throws Exception;
}