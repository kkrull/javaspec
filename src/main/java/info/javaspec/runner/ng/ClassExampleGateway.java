package info.javaspec.runner.ng;

import org.junit.runner.Description;

public final class ClassExampleGateway implements NewExampleGateway {
  public ClassExampleGateway(Class<?> contextClass) {
  }

  @Override
  public String rootContextName() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean hasExamples() {
    throw new UnsupportedOperationException("work here");
  }

  @Override
  public int totalNumExamples() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Description junitDescriptionTree() {
    throw new UnsupportedOperationException();
  }
}
