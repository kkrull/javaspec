package org.javaspec.runner;

import java.util.List;
import java.util.stream.Stream;

interface ExampleGateway {
  List<Throwable> findInitializationErrors();
  
  //Context
  Context getRootContext();
  String getRootContextName();
  List<Context> getSubContexts(Context context);
  
  //Examples
  Stream<Example> getExamples();
  boolean hasExamples();
}