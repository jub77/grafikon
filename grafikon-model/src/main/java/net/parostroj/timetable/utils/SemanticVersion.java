package net.parostroj.timetable.utils;

/**
 * Version of the program. For parsing purposes.
 *
 * @author jub
 */
public class SemanticVersion {

    private final int major;
    private final int minor;
    private final int patch;

    private final String prerelease;
    private final String build;

    public SemanticVersion(String version) {
        int plusIndex = version.indexOf('+');
        if (plusIndex != -1) {
            build = version.substring(plusIndex + 1);
            version = version.substring(0, plusIndex);
        } else {
            build = null;
        }
        int hyphenIndex = version.indexOf('-');
        if (hyphenIndex != -1) {
            prerelease = version.substring(hyphenIndex + 1);
            version = version.substring(0, hyphenIndex);
        } else {
            prerelease = null;
        }
        String[] parts = version.split("\\.");
        major = Integer.parseInt(parts[0]);
        minor = Integer.parseInt(parts[1]);
        patch = Integer.parseInt(parts[2]);
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public int getPatch() {
        return patch;
    }

    public String getPrerelease() {
        return prerelease;
    }

    public String getBuild() {
        return build;
    }

    public String toBaseVersionString() {
        return String.format("%d.%d.%d", major, minor, patch);
    }

    public String toVersionString() {
        if (prerelease != null) {
            StringBuilder builder = new StringBuilder(toBaseVersionString());
            builder.append('-').append(prerelease);
            return builder.toString();
        } else {
            return toBaseVersionString();
        }
    }

    public String toCompleteVersionString() {
        if (build != null) {
            StringBuilder builder = new StringBuilder(toVersionString());
            builder.append('+').append(build);
            return builder.toString();
        } else {
            return toVersionString();
        }
    }

    @Override
    public String toString() {
        return toCompleteVersionString();
    }
}
