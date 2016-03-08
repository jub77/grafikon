package net.parostroj.timetable.utils;

import java.util.ResourceBundle;

public class VersionInfo {

	public enum Type { SHORT, NORMAL, FULL }

    private final ResourceBundle bundle;

    public VersionInfo() {
        bundle = ResourceBundle.getBundle("grafikon_version");
    }

    public String getVersion(Type type) {
    	String version = null;
    	switch (type) {
    		case SHORT:
    			version = bundle.getString("grafikon.version.short");
    			break;
    		case NORMAL:
    			version = bundle.getString("grafikon.version");
    			break;
    		case FULL:
    			version = bundle.getString("grafikon.version.complete");
    			break;
    	}
    	return version;
    }

    public String getVersion() {
        return getVersion(Type.NORMAL);
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
