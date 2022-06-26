package net.parostroj.timetable.utils;

import de.skuzzle.semantic.Version;
import java.util.Map;

import net.parostroj.timetable.utils.ManifestVersionInfo.VersionData;

public class VersionInfo {

    private static final Version NO_MANIFEST_VERSION = Version.parseVersion("0.0.0-no.manifest");

    private final Version version;

    private final Map<String, VersionData> versions;

    public VersionInfo() {
        versions = new ManifestVersionInfo().getManifestVersions();
        VersionData modelData = versions.get("grafikon-model");
        if (modelData == null) {
            version = NO_MANIFEST_VERSION;
        } else {
            version = modelData.getVersion();
        }
    }

    public Version getVersionWithoutBuild() {
        Version completeVersion = getVersion();
        return completeVersion.withBuildMetaData("");
    }

    public Version getVersion() {
        return version;
    }

    public Map<String, VersionData> getVersions() {
        return versions;
    }
}
