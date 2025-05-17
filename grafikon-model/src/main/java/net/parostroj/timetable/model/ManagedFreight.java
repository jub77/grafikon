package net.parostroj.timetable.model;

/**
 * Type of managed freight for train.
 */
public enum ManagedFreight {
    NONE("none"), ALL("all"), ENDS("ends");

    private final String key;

    ManagedFreight(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public static ManagedFreight fromString(String id) {
        for (ManagedFreight managedFreight : values()) {
            if (managedFreight.toString().equals(id)) {
                return managedFreight;
            }
        }
        return null;
    }
}
