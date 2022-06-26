package net.parostroj.plugin.version

import de.skuzzle.semantic.Version
import groovy.transform.CompileStatic
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import org.ajoberstar.grgit.Grgit
import org.ajoberstar.grgit.gradle.GrgitPlugin
import org.gradle.api.*

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

@CompileStatic
class ParsedVersion {
	String tag
	int since
	String hash
	boolean forced

	static ParsedVersion parse(String commit, Grgit grgit) {
		try {
			def describe = grgit.describe([longDescr: true, tags: true, commit: commit, match: ["*.*.*"]] as Map<String, Object>)
			def match = describe =~ /^(.*)-(\d*)-g([a-f\d]*)$/
			if (!match) {
				return null
			} else {
				return new ParsedVersion(tag: match.group(1), since: match.group(2) as int, hash: match.group(3))
			}
		} catch (Exception ignored) {
			// failed describe - fallback to null
			return null
		}
	}
}

@CompileStatic
class VersionPlugin implements Plugin<Project> {

	private static ParsedVersion getForcedVersion(Project project) {
		String forcedVersion = project.findProperty("forceVersion")
		if (forcedVersion) {
			return new ParsedVersion(tag: forcedVersion, since: 0, forced: true)
		} else {
			return null
		}
	}

	void apply(Project project) {
		project.pluginManager.apply(GrgitPlugin)

		VersionExtension ver = project.extensions.create("scmVersion", VersionExtension)
		Grgit grgit = project.extensions.getByType(Grgit)
		def head = grgit.head()
		ver.dirty = !grgit.status().clean

		def formatter = DateTimeFormatter.ofPattern('yyyyMMddHHmm')
		def commitTimestamp = formatter.format(head.dateTime.toInstant().atZone(ZoneOffset.UTC))
		def commitId = head.id.substring(0, 12)
		ver.buildHash = commitId
		ver.buildTimestamp = formatter.format(Instant.now().atOffset(ZoneOffset.UTC))
		ver.buildId = "${ver.buildTimestamp}-${commitId}"
		ver.year = DateTimeFormatter.ofPattern("yyyy").format(head.dateTime)

		def tagVersion
		def prerelease

		def parsedVersion = getForcedVersion(project) ?: ParsedVersion.parse('HEAD', grgit)

		if (parsedVersion == null) {
			tagVersion = Version.parseVersion("0.0.0")
			ver.snapshot = true
			prerelease = false
		} else {
			tagVersion = Version.parseVersion(parsedVersion.tag)
			ver.since = parsedVersion.since
			ver.snapshot = ver.since != 0
			if ("alfa" == tagVersion.preRelease) {
				ver.snapshot = true
			}
			if (ver.since == 0 && !parsedVersion.forced) {
				def previousParsedVersion = ParsedVersion.parse('HEAD~1', grgit)
				if (previousParsedVersion != null) {
					ver.sincePrevious = previousParsedVersion.since
				}
			}
			prerelease = tagVersion.preRelease as Boolean
		}

		def baseVersion
		if (ver.snapshot) {
			if (prerelease) {
				baseVersion = tagVersion.toStable()
				ver.baseVersion = tagVersion.toStable()
			} else {
				baseVersion = tagVersion.nextPatch().toString()
				ver.baseVersion = tagVersion.nextPatch().toStable()
			}
		} else {
			baseVersion = tagVersion.toString()
			ver.baseVersion = tagVersion.toStable()
		}

		ver.projectVersion = "${baseVersion}${ver.snapshot ? '-SNAPSHOT' : ''}"
		def dirtySuffix = ver.dirty ? '.dirty' : ''
		ver.shortVersion = ver.snapshot
				? "${baseVersion}-dev.${commitTimestamp}${dirtySuffix}"
				: "${baseVersion}${dirtySuffix}"
		ver.distVersion = "${ver.shortVersion}+${ver.buildId}"

		project.tasks.register('version', { Task t ->
			t.doLast {
				VersionExtension scmVersion = project.extensions.getByType(VersionExtension)
				project.logger.lifecycle("Base version: {}", scmVersion.baseVersion)
				project.logger.lifecycle("Tag version: {}", tagVersion)
				project.logger.lifecycle("Project version: {}", scmVersion.projectVersion)
				project.logger.lifecycle("Short version: {}", scmVersion.shortVersion)
				project.logger.lifecycle("Dist version: {}", scmVersion.distVersion)
				project.logger.lifecycle("Snapshot: {}", scmVersion.snapshot)
				project.logger.lifecycle("Dirty: {}", scmVersion.dirty)
				project.logger.lifecycle("Since: {}", scmVersion.since)
				project.logger.lifecycle("Since previous: {}", scmVersion.sincePrevious)
				project.logger.lifecycle("Build timestamp: {}", scmVersion.buildTimestamp)
				project.logger.lifecycle("Build id: {}", scmVersion.buildId)
				project.logger.lifecycle("Build hash: {}", scmVersion.buildHash)
			}
		})
	}
}
