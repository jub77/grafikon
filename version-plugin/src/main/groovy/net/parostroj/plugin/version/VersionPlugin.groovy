package net.parostroj.plugin.version

import com.github.zafarkhaja.semver.*
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

	String buildTimestamp
	String buildId
	String buildHash

	String projectVersion
	String distVersion
	String baseVersion
	String shortVersion
}

@CompileStatic
class VersionPlugin implements Plugin<Project> {

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

		def describe = grgit.describe(Collections.singletonMap("longDescr", (Object) Boolean.TRUE))

		def tagVersion
		def prerelease

		def match = describe =~ /^(.*)-(\d*)-g([a-f0-9]*)$/
		if (!match) {
			tagVersion = Version.valueOf("0.0.0")
			ver.snapshot = true
			prerelease = false
		} else {
			tagVersion = Version.valueOf(match.group(1))
			ver.snapshot = match.group(2) != "0"
			if ("alfa" == tagVersion.preReleaseVersion) {
				ver.snapshot = true
			}
			prerelease = tagVersion.preReleaseVersion as Boolean
		}

		def baseVersion
		if (ver.snapshot) {
			if (prerelease) {
				baseVersion = tagVersion.normalVersion
				ver.baseVersion = tagVersion.normalVersion
			} else {
				baseVersion = tagVersion.incrementPatchVersion().toString()
				ver.baseVersion = tagVersion.incrementPatchVersion().normalVersion
			}
		} else {
			baseVersion = tagVersion.toString()
			ver.baseVersion = tagVersion.normalVersion
		}

		ver.projectVersion = "${baseVersion}${ver.snapshot ? '-SNAPSHOT' : ''}"
		def dirtySuffix = ver.dirty ? '.dirty' : ''
		ver.shortVersion = ver.snapshot
				? "${baseVersion}-dev.${commitTimestamp}${dirtySuffix}"
				: "${baseVersion}${dirtySuffix}"
		ver.distVersion = "${ver.shortVersion}+${ver.buildId}"

		project.tasks.create('version', { Task t ->
			t.doLast {
				VersionExtension scmVersion = project.extensions.getByType(VersionExtension)
				project.logger.lifecycle("Base version: {}", scmVersion.baseVersion)
				project.logger.lifecycle("Git describe: {}", describe)
				project.logger.lifecycle("Tag version: {}", tagVersion)
				project.logger.lifecycle("Project version: {}", scmVersion.projectVersion)
				project.logger.lifecycle("Short version: {}", scmVersion.shortVersion)
				project.logger.lifecycle("Dist version: {}", scmVersion.distVersion)
				project.logger.lifecycle("Snapshot: {}", scmVersion.snapshot)
				project.logger.lifecycle("Dirty: {}", scmVersion.dirty)
				project.logger.lifecycle("Build timestamp: {}", scmVersion.buildTimestamp)
				project.logger.lifecycle("Build id: {}", scmVersion.buildId)
				project.logger.lifecycle("Build hash: {}", scmVersion.buildHash)
			}
		})
	}
}
