package net.parostroj.plugin.version

import groovy.transform.CompileStatic

@CompileStatic
class VersionExtension {
	boolean dirty
	boolean snapshot
	int since
	int sincePrevious

	String buildTimestamp
	String buildId
	String buildHash

	String projectVersion
	String distVersion
	String baseVersion
	String shortVersion

	String year
}