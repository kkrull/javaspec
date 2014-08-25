package org.javaspec.runner;

import java.util.List;

interface ExampleGateway {
  List<Throwable> findInitializationErrors();
  Context getContextRoot();
  List<String> getExampleNames(Context context);
}