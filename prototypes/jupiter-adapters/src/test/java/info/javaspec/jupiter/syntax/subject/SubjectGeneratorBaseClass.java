package info.javaspec.jupiter.syntax.subject;

import java.util.function.Supplier;

abstract class SubjectGeneratorBaseClass<S> {
	private Supplier<S> subject;

	protected S makeSubject() {
		return this.subject.get();
	}

	protected void subjectGenerator(Supplier<S> subject) {
		this.subject = subject;
	}
}
