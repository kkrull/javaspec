package info.javaspec.jupiter.syntax.subject;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import info.javaspec.jupiter.Greeter;

//Positive: There's a type-safe way to get/set the subject.
//Positive: Subject methods can be called from anywhere.  Even lambdas?
//Negative: Specs have to extend that one base class, to work with subjects.
class SubjectFromBaseClassSpecs extends JavaSpec<Greeter> {
  @BeforeEach
  void setup() {
    setSubject(new Greeter());
  }

  @Test
  void greetsTheWorld() {
    Greeter subject = getSubject();
    assertEquals("Hello world!", subject.makeGreeting());
  }

  @Test
  void greetsThePerson() {
    Greeter subject = getSubject();
    assertEquals("Hello George!", subject.makeGreeting("George"));
  }
}
