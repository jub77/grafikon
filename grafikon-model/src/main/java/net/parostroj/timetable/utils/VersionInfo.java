package net.parostroj.timetable.utils;

import java.util.Map;

import com.github.zafarkhaja.semver.Version;

import net.parostroj.timetable.utils.ManifestVersionInfo.VersionData;

public class VersionInfo {

    private static final Version NO_MANIFEST_VERSION = Version.valueOf("0.0.0-no.manifest");

    private final Version version;

    private Map<String, VersionData> versions;

    public VersionInfo() {
        versions = new ManifestVersionInfo().getManifestVersions();
        VersionData modelData = versions.get("grafikon-model");
        if (modelData == null) {
            version = NO_MANIFEST_VERSION;
        } else {
            version = modelData.getVersion();
        }
    }

    public Version getVersion() {
        return version;
    }

    public Map<String, VersionData> getVersions() {
        return versions;
    }
}
