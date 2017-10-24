package net.parostroj.timetable.utils;

import java.util.Map;

import net.parostroj.timetable.utils.ManifestVersionInfo.VersionData;

public class VersionInfo {

    private static final SemanticVersion NO_MANIFEST_VERSION = new SemanticVersion("0.0.0-no.manifest");

    private final SemanticVersion version;

    public VersionInfo() {
        Map<String, VersionData> manifests = new ManifestVersionInfo().getManifestVersions();
        VersionData modelData = manifests.get("grafikon-model");
        if (modelData == null) {
            version = NO_MANIFEST_VERSION;
        } else {
            version = new SemanticVersion(modelData.getVersion());
        }
    }

    public SemanticVersion getVersion() {
        return version;
    }
}
