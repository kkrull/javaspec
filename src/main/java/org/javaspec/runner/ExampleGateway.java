package org.javaspec.runner;

import java.util.List;
import java.util.stream.Stream;

interface ExampleGateway {
  List<Throwable> findInitializationErrors();
  Stream<NewExample> getExamples();
  List<String> getExampleNames(Context context);
  Context getRootContext();
}