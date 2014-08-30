package org.javaspec.runner;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

interface ExampleGateway {
  List<Throwable> findInitializationErrors();
  
  //Context
  Context getRootContext();
  String getRootContextName();
  Set<Context> getSubContexts(Context context);
  
  //Examples
  Stream<Example> getExamples();
  boolean hasExamples();
}