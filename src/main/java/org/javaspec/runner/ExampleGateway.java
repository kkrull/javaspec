package org.javaspec.runner;

import java.util.List;
import java.util.stream.Stream;

interface ExampleGateway {
  List<Throwable> findInitializationErrors();
  
  //Context
  Context getRootContext();
  String getRootContextName();
  
  //Examples
  List<String> getExampleNames(Context context);
  Stream<NewExample> getExamples();
  boolean hasExamples();
}