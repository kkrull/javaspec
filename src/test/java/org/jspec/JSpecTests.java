package org.jspec;

import org.junit.Test;

public class JSpecTests {
	@Test
	public void nativeJUnitTest() {	throw new TestRanException();	}
	
	@SuppressWarnings("serial")
	// Nothing to version; only used in 1 place
	public final class TestRanException extends RuntimeException {}
}