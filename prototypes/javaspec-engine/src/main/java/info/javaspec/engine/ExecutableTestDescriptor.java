package info.javaspec.engine;

import org.junit.platform.engine.EngineExecutionListener;
import org.junit.platform.engine.TestDescriptor;

//Adapter for JavaSpec containers or tests that runs on Jupiter.
interface ExecutableTestDescriptor extends TestDescriptor {
	void execute(EngineExecutionListener listener);
}
