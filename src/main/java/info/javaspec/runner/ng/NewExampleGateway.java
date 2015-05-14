package info.javaspec.runner.ng;

interface NewExampleGateway {
  Class<?> getContextClass();
  boolean hasExamples();
  int numExamples();
}
