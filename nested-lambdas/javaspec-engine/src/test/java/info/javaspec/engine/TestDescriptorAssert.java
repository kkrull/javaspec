/**
 * MIT License
 *
 * Copyright (c) 2014–2022 Kyle Krull
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package info.javaspec.engine;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.UniqueId.Segment;

//Assertions on Jupiter TestDescriptors
public class TestDescriptorAssert extends AbstractAssert<TestDescriptorAssert, TestDescriptor> {
	public static TestDescriptorAssert assertThat(TestDescriptor actual) {
		return new TestDescriptorAssert(actual);
	}

	private TestDescriptorAssert(TestDescriptor testDescriptor) {
		super(testDescriptor, TestDescriptorAssert.class);
	}

	public TestDescriptorAssert hasChildren(int expectedNum) {
		isNotNull();

		Set<? extends TestDescriptor> actualChildren = actual.getChildren();
		if (actualChildren.size() != expectedNum) {
			failWithMessage(
				"Expected TestDescriptor to have <%d> children but has <%d>: <%s>",
				expectedNum,
				actualChildren.size(),
				actualChildren
			);
		}

		return this;
	}

	public TestDescriptorAssert hasChildrenNamed(String... displayNames) {
		isNotNull();
		List<String> actualNames = actual.getChildren()
			.stream()
			.map(x -> x.getDisplayName())
			.collect(Collectors.toList());

		Assertions.assertThat(actualNames)
			.describedAs(
				"Expected TestDescriptor <%s> to have children with display names",
				actual.getUniqueId()
			)
			.containsExactly(displayNames);
		return this;
	}

	public TestDescriptorAssert hasDisplayName(String expected) {
		isNotNull();
		if (!Objects.equals(actual.getDisplayName(), expected)) {
			failWithMessage(
				"Expected TestDescriptor to have display name <%s> but was <%s>",
				expected,
				actual.getDisplayName()
			);
		}

		return this;
	}

	public TestDescriptorAssert hasIdEndingIn(String type, String value) {
		isNotNull();
		Segment actualSegment = actual.getUniqueId().getLastSegment();

		if (!Objects.equals(actualSegment.getType(), type)
			|| !Objects.equals(actualSegment.getValue(), value)) {
			failWithMessage(
				"Expected TestDescriptor's UniqueId to end in <%s:%s> but was <%s>",
				type,
				value,
				actualSegment
			);
		}

		return this;
	}

	public TestDescriptorAssert hasNoChildren() {
		isNotNull();
		if (!Objects.equals(actual.getChildren(), Collections.emptySet())) {
			failWithMessage(
				"Expected TestDescriptor to have no children but was <%s>",
				actual.getChildren()
			);
		}

		return this;
	}

	public TestDescriptorAssert hasParent(TestDescriptor expectedParent) {
		isNotNull();

		Optional<TestDescriptor> actualParent = actual.getParent();
		if (actualParent.isEmpty()) {
			failWithMessage(
				String.format(
					"Expected TestDescriptor to have parent named <%s>, but was null",
					expectedParent
				)
			);
		}

		if (!Objects.equals(actualParent.orElseThrow(), expectedParent)) {
			failWithMessage(
				"Expected TestDescriptor to have parent <%s> but was <%s>",
				expectedParent,
				actualParent.orElseThrow()
			);
		}

		return this;
	}

	public TestDescriptorAssert hasUniqueId(UniqueId expected) {
		isNotNull();
		if (!Objects.equals(actual.getUniqueId(), expected)) {
			failWithMessage(
				"Expected TestDescriptor to have UniqueId <%s> but was <%s>",
				expected,
				actual.getUniqueId()
			);
		}

		return this;
	}

	public TestDescriptorAssert isJustATest() {
		isNotNull();
		if (actual.isContainer())
			failWithMessage("Expected TestDescriptor to not be a container that can contain other descriptors");
		if (actual.isRoot())
			failWithMessage("Expected TestDescriptor to not be a root container that contains everything else");
		if (!actual.isTest())
			failWithMessage("Expected TestDescriptor to be a test");

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
