/*
 * Copyright (c) 2014–2022 Kyle Krull.
 * All rights reserved.
 */

package info.javaspec.api;

//A procedure that verifies the behavior under test
@FunctionalInterface
public interface Verification {
	void execute();
}
