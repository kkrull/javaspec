package info.javaspec.runner.old;

interface Example {
  String getContextName();
  String getName();
  boolean isSkipped();
  void run() throws Exception;
}