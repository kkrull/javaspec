package info.javaspec.lang.lambda;

import info.javaspec.Spec;
import info.javaspec.SequentialSuite;
import info.javaspec.Suite;

import java.util.Stack;

/** Groups recently-declared specs into a suite of specs that can be run together */
final class SpecDeclaration {
  private static SpecDeclaration _instance;
  private final Stack<SequentialSuite> currentSuite;

  public static SpecDeclaration getInstance() {
    return _instance;
  }

  public static void beginDeclaration() {
    if(_instance != null)
      throw new IllegalStateException("Specs are already being declared");

    _instance = new SpecDeclaration(initRootSuite());
  }

  public static Suite endDeclaration() {
    Suite suite = _instance.currentSuite.pop();
    if(!_instance.currentSuite.isEmpty())
      throw new IllegalStateException("Spec declaration ended prematurely");

    _instance = null;
    return suite;
  }

  private SpecDeclaration(Stack<SequentialSuite> currentSuite) {
    this.currentSuite = currentSuite;
  }

  public void declareSpecsFor(String subject, BehaviorDeclaration describeBehavior) {
    SequentialSuite subjectSuite = new SequentialSuite(subject);
    currentSuite().addChildSuite(subjectSuite); //Add the child suite in line with any other declared specs
    this.currentSuite.push(subjectSuite); //Push on to the stack in case there are nested describes
    describeBehavior.declareSpecs();
    this.currentSuite.pop();
  }

  public void createSpec(String intendedBehavior, BehaviorVerification verification) {
    Spec spec = new DescriptiveSpec(intendedBehavior, verification);
    currentSuite().addSpec(spec);
  }

  private static Stack<SequentialSuite> initRootSuite() {
    Stack<SequentialSuite> currentSuite = new Stack<>();
    currentSuite.push(new SequentialSuite());
    return currentSuite;
  }

  private SequentialSuite currentSuite() {
    return this.currentSuite.peek();
  }
}
