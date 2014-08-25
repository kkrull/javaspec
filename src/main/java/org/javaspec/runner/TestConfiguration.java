package org.javaspec.runner;

import java.util.List;
import java.util.stream.Stream;

public interface TestConfiguration { //TODO KDK: Remove, if no longer needed
  List<Throwable> findInitializationErrors();
  Class<?> getContextClass(); 
  Stream<Example> getExamples();
}