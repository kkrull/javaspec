package org.jspec.runner;

import java.util.List;

interface TestConfiguration {
  List<Throwable> findInitializationErrors();
  boolean hasInitializationErrors();
  Class<?> getContextClass();
  List<Example> getExamples();
}