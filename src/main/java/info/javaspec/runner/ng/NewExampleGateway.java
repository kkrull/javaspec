package info.javaspec.runner.ng;

import org.junit.runner.Description;

interface NewExampleGateway {
  String rootContextName();
  boolean hasExamples();
  int totalNumExamples();

  Description junitDescriptionTree();
}
