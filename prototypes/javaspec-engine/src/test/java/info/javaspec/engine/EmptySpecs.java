package info.javaspec.engine;

import info.javaspec.api.JavaSpec;
import info.javaspec.api.SpecClass;

//An empty class that has no specs in it.
public class EmptySpecs implements SpecClass {
	@Override
	public void declareSpecs(JavaSpec javaspec) {
		/* empty */ }
}
