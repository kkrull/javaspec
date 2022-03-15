package info.javaspec.jupiter.syntax.subject;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

//Positive: There's a type-safe way to get/set the subject.
//Positive: Subject methods can be called from anywhere, even lambdas.
//Negative: Specs have to extend that one base class, to work with subjects.
@DisplayName("Subject syntax: Store subjects in a field in a base class")
class SubjectFieldBaseClassSpecs extends SubjectFieldBaseClass<List<String>> {
	// Negative: Re-initializes subject per TestFactory (not per spec), making it
	// unsuited for stateful subjects.
	@BeforeEach
	void setup() {
		setSubject(new LinkedList<String>());
	}

	@TestFactory
	DynamicNode listSpecs() {
		return DynamicContainer.dynamicContainer(
			"List",
			Arrays.asList(
				DynamicTest.dynamicTest("appends to the tail", () ->
				{
					List<String> subject = getSubject();
					subject.clear(); // Negative: Have to re-initialize state here, all over again

					subject.add("append-a");
					subject.add("append-b");
					assertEquals(Arrays.asList("append-a", "append-b"), subject);
				}),
				DynamicTest.dynamicTest("prepends to the head", () ->
				{
					List<String> subject = getSubject();
					subject.clear(); // Negative: Have to re-initialize state here, all over again

					subject.add(0, "prepend-a");
					subject.add(0, "prepend-b");
					assertEquals(Arrays.asList("prepend-b", "prepend-a"), subject);
				})
			)
		);
	}
}
