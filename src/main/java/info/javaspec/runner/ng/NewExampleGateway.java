package info.javaspec.runner.ng;

import java.util.List;

interface NewExampleGateway {
  boolean hasExamples();
  List<String> exampleFieldNames(Class<?> context);
  int totalNumExamples();

  Class<?> rootContextClass();
  List<Class<?>> subContextClasses(Class<?> context);
}
