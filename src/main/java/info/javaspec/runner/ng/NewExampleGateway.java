package info.javaspec.runner.ng;

import java.util.List;

interface NewExampleGateway {
  Class<?> getContextClass();
  List<String> exampleNames();
  boolean hasExamples();
  int numExamples();
}
