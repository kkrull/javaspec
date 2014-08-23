package org.javaspec.runner;

import java.util.List;
import java.util.stream.Stream;

interface ExampleGateway {
  List<Throwable> findInitializationErrors();
  Context getContextRoot();
  List<String> getExampleNames(Context context);
  
  Class<?> getContextClass(); //TODO KDK: Remove, if no longer needed
  Stream<Example> getExamples(); //TODO KDK: Remove, if no longer needed
}