package org.jspec.runner;

import java.util.List;
import java.util.stream.Stream;

interface TestConfiguration {
  List<Throwable> findInitializationErrors();
  Class<?> getContextClass();
  Stream<Example> getExamples();
}