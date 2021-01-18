package info.javaspec.jupiter.syntax.subject;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import info.javaspec.jupiter.Greeter;

//Positive: There's a type-safe way to get/set the subject.
//Positive: Subject methods can be called from anywhere.  Even lambdas?
//Negative: Specs have to extend that one base class, to work with subjects.
class SubjectFromBaseClassSpecs extends JavaSpec<Greeter> {
  @BeforeEach
  void setup() {
    setSubject(new Greeter());
  }

  @TestFactory
  DynamicNode greetsTheWorld() {
    return DynamicTest.dynamicTest("Greets the world", () -> {
      Greeter subject = getSubject();
      assertEquals("Hello world!", subject.makeGreeting());
    });
  }

  @TestFactory
  DynamicNode greetsThePerson() {
    return DynamicTest.dynamicTest("Greets a person", () -> {
      Greeter subject = getSubject();
      assertEquals("Hello George!", subject.makeGreeting("George"));
    });
  }
}
