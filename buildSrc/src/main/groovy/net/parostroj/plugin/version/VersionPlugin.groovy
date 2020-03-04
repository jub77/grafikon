package net.parostroj.plugin.version

import com.github.zafarkhaja.semver.*
import org.gradle.api.*

class VersionExtension {
	def dirty
	def snapshot

	def buildTimestamp
	def buildId

	def projectVersion
	def distVersion
	def baseVersion
}

class VersionPlugin implements Plugin<Project> {
	void apply(Project project) {
		project.pluginManager.apply(org.ajoberstar.grgit.gradle.GrgitPlugin)

		def ver = project.extensions.create("scmVersion", VersionExtension.class)
		def head = project.grgit.head()
		ver.dirty = !project.grgit.status().clean

		def commitTimestamp = Date.from(head.dateTime.toInstant()).format('yyyyMMddHHmm')
		def commitId = head.id.substring(0, 12)
		ver.buildTimestamp = new Date().format('yyyyMMddHHmm')
		ver.buildId = "${ver.buildTimestamp}-${commitId}"

		def describe = project.grgit.describe(longDescr: true)

		def tagVersion
		def prerelease

		def match = describe =~ /^(.*)-(\d*)-g([a-f0-9]*)$/
		if (!match) {
			tagVersion = Version.valueOf("0.0.0")
			ver.snapshot = true
			prerelease = false
		} else {
			tagVersion = Version.valueOf(match[0][1])
			ver.snapshot = match[0][2] != "0"
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
		ver.distVersion = ver.snapshot
			? "${baseVersion}-dev.${commitTimestamp}${dirtySuffix}+${ver.buildId}"
			: "${baseVersion}${dirtySuffix}+${ver.buildId}"

		project.tasks.create('version', {
			doLast {
				project.logger.lifecycle("Base version: {}", project.scmVersion.baseVersion)
				project.logger.lifecycle("Git describe: {}", describe)
				project.logger.lifecycle("Tag version: {}", tagVersion)
				project.logger.lifecycle("Project version: {}", project.scmVersion.projectVersion)
				project.logger.lifecycle("Dist version: {}", project.scmVersion.distVersion)
				project.logger.lifecycle("Snapshot: {}", project.scmVersion.snapshot)
				project.logger.lifecycle("Dirty: {}", project.scmVersion.dirty)
				project.logger.lifecycle("Build timestamp: {}", project.scmVersion.buildTimestamp)
				project.logger.lifecycle("Build id: {}", project.scmVersion.buildId)
			}
		})
	}
}
