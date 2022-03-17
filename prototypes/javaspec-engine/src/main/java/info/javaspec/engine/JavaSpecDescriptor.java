package info.javaspec.engine;

import org.junit.platform.engine.EngineExecutionListener;

//Adapter for a JavaSpec container or test of some sort that works likes its Jupiter counterpart.
interface JavaSpecDescriptor {
	void execute(EngineExecutionListener listener);
}
