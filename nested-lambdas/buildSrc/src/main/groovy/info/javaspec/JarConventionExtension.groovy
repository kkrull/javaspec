package info.javaspec

import org.gradle.api.file.RegularFileProperty

interface JarConventionExtension {
	RegularFileProperty getLicenseFile()
}
