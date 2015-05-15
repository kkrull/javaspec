package info.javaspec.runner.ng;

import java.util.List;

//TODO KDK: Abstract the notion of the context name?
interface NewExampleGateway {
  boolean hasExamples();
  List<String> exampleNames(Class<?> context);
  int totalExamples();

  Class<?> rootContextClass();
  List<Class<?>> subContextClasses(Class<?> context);
}
