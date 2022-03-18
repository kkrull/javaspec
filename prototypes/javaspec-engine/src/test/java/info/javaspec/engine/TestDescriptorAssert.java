package info.javaspec.engine;

import java.util.Collections;
import java.util.Objects;
import org.assertj.core.api.AbstractAssert;
import org.junit.platform.engine.TestDescriptor;

//Assertions on Jupiter TestDescriptors
public class TestDescriptorAssert extends AbstractAssert<TestDescriptorAssert, TestDescriptor> {
	public static TestDescriptorAssert assertThat(TestDescriptor actual) {
		return new TestDescriptorAssert(actual);
	}

	public TestDescriptorAssert(TestDescriptor testDescriptor) {
		super(testDescriptor, TestDescriptorAssert.class);
	}

	public TestDescriptorAssert hasNoChildren() {
		isNotNull();
		if (!Objects.equals(actual.getChildren(), Collections.emptySet())) {
			failureWithActualExpected(
				actual.getChildren(),
				Collections.emptySet(),
				"Expected TestDescriptor to have no children"
			);
		}

		return this;
	}

	public TestDescriptorAssert isRegularContainer() {
		isNotNull();
		if (!actual.isContainer())
			failWithMessage("Expected TestDescriptor to be a container that can contain other descriptors");
		if (actual.isRoot())
			failWithMessage("Expected TestDescriptor not to be a root container");
		if (actual.isTest())
			failWithMessage("Expected TestDescriptor not to be a test");

		return this;
	}

	public TestDescriptorAssert isRootContainer() {
		isNotNull();
		if (!actual.isContainer())
			failWithMessage("Expected TestDescriptor to be a container that can contain other descriptors");
		if (!actual.isRoot())
			failWithMessage("Expected TestDescriptor to be a root container that contains everything else");
		if (actual.isTest())
			failWithMessage("Expected TestDescriptor not to be a test");

		return this;
	}
}
