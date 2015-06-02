package info.javaspec.runner.old;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public interface ExampleGateway {
  List<Throwable> findInitializationErrors();
  
  //Context
  Context getRootContext();
  String getRootContextName();
  Set<Context> getSubContexts(Context context);
  
  //Examples
  Stream<Example> getExamples();
  boolean hasExamples();
}