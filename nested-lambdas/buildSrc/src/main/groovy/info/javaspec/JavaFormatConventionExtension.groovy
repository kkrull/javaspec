package info.javaspec

import org.gradle.api.file.RegularFileProperty

interface JavaFormatConventionExtension {
	RegularFileProperty getEclipseConfigFile()
}
