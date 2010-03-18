package net.parostroj.timetable.model.ls;

/**
 * Model version.
 * 
 * @author jub
 */
public class ModelVersion implements Comparable<ModelVersion> {
    private final String version;
    
    private final int majorVersion;
    
    private final int minorVersion;

    public ModelVersion(String version) {
        this.version = version;
        String parts[] = version.split("\\.");
        majorVersion = Integer.parseInt(parts[0]);
        minorVersion = Integer.parseInt(parts[1]);
    }
    
    public ModelVersion(int majorVersion, int minorVersion) {
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
        version = String.format("%d.%d", majorVersion, minorVersion);
    }

    public int getMajorVersion() {
        return majorVersion;
    }

    public int getMinorVersion() {
        return minorVersion;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return version;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ModelVersion other = (ModelVersion) obj;
        if (this.majorVersion != other.majorVersion) {
            return false;
        }
        if (this.minorVersion != other.minorVersion) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + this.majorVersion;
        hash = 47 * hash + this.minorVersion;
        return hash;
    }

    @Override
    public int compareTo(ModelVersion o) {
        if (this.majorVersion == o.majorVersion) {
            if (this.minorVersion == o.minorVersion)
                return 0;
            else if (this.minorVersion < o.minorVersion)
                return -1;
            else
                return 1;
        } else if (this.majorVersion < o.majorVersion)
            return -1;
        else
            return 1;
    }
}
