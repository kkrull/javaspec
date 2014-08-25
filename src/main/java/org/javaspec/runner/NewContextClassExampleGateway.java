package org.javaspec.runner;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

final class NewContextClassExampleGateway implements ExampleGateway {
  NewContextClassExampleGateway(Class<?> contextClass) {
//    this.contextClass = contextClass;
  }

  @Override
  public List<Throwable> findInitializationErrors() {
    return Collections.emptyList();
//    throw new UnsupportedOperationException();
  }

  @Override
  public Context getContextRoot() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Stream<NewExample> getExamples() {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<String> getExampleNames(Context context) {
    throw new UnsupportedOperationException();
  }
}