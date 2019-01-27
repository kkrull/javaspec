package info.javaspec.lang.lambda;

import info.javaspec.Spec;
import info.javaspec.SequentialSuite;
import info.javaspec.Suite;

import java.util.Stack;

/** Groups recently-declared specs into a suite of specs that can be run together */
final class SpecDeclaration {
  private static final Stack<SequentialSuite> _currentSuite = new Stack<>();

  public static void beginDeclaration() {
    if(!_currentSuite.isEmpty())
      throw new IllegalStateException("Specs are already being declared");

    _currentSuite.push(new SequentialSuite());
  }

  public static void declareSpecsFor(String subject, BehaviorDeclaration describeBehavior) {
    SequentialSuite subjectSuite = new SequentialSuite(subject);
    _currentSuite.peek().addChildSuite(subjectSuite); //Add the child suite in line with any other declared specs
    _currentSuite.push(subjectSuite); //Push on to the stack in case there are nested describes
    describeBehavior.declareSpecs();
    _currentSuite.pop();
  }

  public static void createSpec(String intendedBehavior, BehaviorVerification verification) {
    Spec spec = new DescriptiveSpec(intendedBehavior, verification);
    _currentSuite.peek().addSpec(spec);
  }

  public static Suite endDeclaration() {
    Suite suite = _currentSuite.pop();
    if(!_currentSuite.isEmpty())
      throw new IllegalStateException("Spec declaration ended prematurely");

    return suite;
  }
}
