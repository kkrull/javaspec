package info.javaspec;

import java.util.List;

/** An ordered collection of Specs */
public interface Suite {
  List<Suite> childSuites();

  String description();

  List<String> intendedBehaviors();

  void runSpecs(SpecReporter reporter);
}
