package org.javaspec.runner;

import java.util.List;
import java.util.stream.Stream;

interface ExampleGateway {
  List<Throwable> findInitializationErrors();
  Class<?> getContextClass();
  Context getContextRoot();
  Stream<Example> getExamples();
}