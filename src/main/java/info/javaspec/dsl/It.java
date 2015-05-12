package info.javaspec.dsl;

/** The Assert part of running a test.  Include one or more of these in each test class. */
@FunctionalInterface
public interface It {
  void run() throws Exception;
}