package net.parostroj.timetable.model.ls;

/**
 * Model version.
 *
 * @author jub
 */
public class ModelVersion implements Comparable<ModelVersion> {

    private static final ModelVersion INITIAL_MODEL_VERSION = new ModelVersion(0, 0, 0);

    private final int majorVersion;
    private final int minorVersion;
    private final int patchVersion;

    public static ModelVersion parseModelVersion(String version) {
        String[] parts = version.split("\\.");
        int majorVersion = Integer.parseInt(parts[0]);
        int minorVersion = Integer.parseInt(parts[1]);
        int patchVersion = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;
        return new ModelVersion(majorVersion, minorVersion, patchVersion);
    }

    public static ModelVersion initialModelVersion() {
        return INITIAL_MODEL_VERSION;
    }

    public ModelVersion(int majorVersion, int minorVersion) {
        this(majorVersion, minorVersion, 0);
    }

    public ModelVersion(int majorVersion, int minorVersion, int patchVersion) {
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
        this.patchVersion = patchVersion;
    }

    public int getMajorVersion() {
        return majorVersion;
    }

    public int getMinorVersion() {
        return minorVersion;
    }

    public int getPatchVersion() {
        return patchVersion;
    }

    public String getVersion() {
        return String.format("%d.%d.%d", majorVersion, minorVersion, patchVersion);
    }

    @Override
    public String toString() {
        return this.getVersion();
    }



    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + majorVersion;
        result = prime * result + minorVersion;
        result = prime * result + patchVersion;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ModelVersion other = (ModelVersion) obj;
        if (majorVersion != other.majorVersion) {
            return false;
        }
        if (minorVersion != other.minorVersion) {
            return false;
        }
        return patchVersion == other.patchVersion;
    }

    @Override
    public int compareTo(ModelVersion o) {
        if (this.majorVersion == o.majorVersion) {
            if (this.minorVersion == o.minorVersion) {
                return Integer.compare(this.patchVersion, o.patchVersion);
            }
            else if (this.minorVersion < o.minorVersion) {
                return -1;
            } else {
                return 1;
            }
        } else if (this.majorVersion < o.majorVersion) {
            return -1;
        } else {
            return 1;
        }
    }
}
