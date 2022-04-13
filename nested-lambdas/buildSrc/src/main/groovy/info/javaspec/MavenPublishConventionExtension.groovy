package info.javaspec

import org.gradle.api.provider.Property

interface MavenPublishConventionExtension {
	Property<String> getPublicationDescription();
	Property<Object> getPublicationFrom();
	Property<String> getPublicationName();
}
