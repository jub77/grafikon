package net.parostroj.plugin.version

import groovy.transform.CompileStatic
import org.ajoberstar.grgit.Grgit

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