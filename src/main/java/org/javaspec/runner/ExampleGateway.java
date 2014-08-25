package org.javaspec.runner;

import java.util.List;
import java.util.stream.Stream;

interface ExampleGateway {
  List<Throwable> findInitializationErrors();
  List<String> getExampleNames(Context context);
  Stream<NewExample> getExamples();
  Context getRootContext();
}