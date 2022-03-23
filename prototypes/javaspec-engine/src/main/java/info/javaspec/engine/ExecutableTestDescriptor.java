package info.javaspec.engine;

import org.junit.platform.engine.EngineExecutionListener;
import org.junit.platform.engine.TestDescriptor;

//Composite adapter for JavaSpec containers or tests that runs on Jupiter and only contains other executable descriptors.
interface ExecutableTestDescriptor extends TestDescriptor {
	void execute(EngineExecutionListener listener);
}
