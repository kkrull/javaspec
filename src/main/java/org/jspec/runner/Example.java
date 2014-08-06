package org.jspec.runner;

interface Example {
  String describeBehavior();
  void run(Object objectDeclaringBehavior) throws Exception;
}