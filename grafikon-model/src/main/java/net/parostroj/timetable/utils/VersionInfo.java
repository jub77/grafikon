package net.parostroj.timetable.utils;

import java.util.ResourceBundle;

public class VersionInfo {

    private final ResourceBundle bundle;

    public VersionInfo() {
        bundle = ResourceBundle.getBundle("grafikon_version");
    }

    public SemanticVersion getVersion() {
        return new SemanticVersion(bundle.getString("grafikon.version"));
    }

    public String getBuild() {
    	return bundle.getString("grafikon.build");
    }

    public String getId() {
    	return bundle.getString("grafikon.id");
    }

    public String getTimestamp() {
    	return bundle.getString("grafikon.timestamp");
    }
}
