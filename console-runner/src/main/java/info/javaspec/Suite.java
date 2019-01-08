package info.javaspec;

import java.util.List;

/** An ordered collection of Specs */
public interface Suite {
  void runSpecs(SpecReporter reporter);

  List<String> specDescriptions();
}
