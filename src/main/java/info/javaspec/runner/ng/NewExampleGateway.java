package info.javaspec.runner.ng;

import java.util.List;

//TODO KDK: Abstract the notion of the context name?
interface NewExampleGateway {
  boolean hasExamples();
  int totalExamples();

  Class<?> rootContextClass();
  List<String> rootContextExampleNames();

  List<Class<?>> subContextClasses(Class<?> context);
  List<String> subContextExampleNames(Class<?> context);
}
