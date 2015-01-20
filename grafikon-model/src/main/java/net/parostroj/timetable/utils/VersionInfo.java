package net.parostroj.timetable.utils;

import java.util.ResourceBundle;

public class VersionInfo {

    private final ResourceBundle bundle;

    public VersionInfo() {
        bundle = ResourceBundle.getBundle("grafikon_version");
    }

    public String getVersion() {
        return bundle.getString("grafikon.version.show");
    }

    public String getFullVersion() {
        return bundle.getString("grafikon.version");
    }
}
