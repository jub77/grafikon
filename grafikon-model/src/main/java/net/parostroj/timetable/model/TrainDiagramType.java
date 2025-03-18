package net.parostroj.timetable.model;

/**
 * Type of diagram.
 */
public enum TrainDiagramType {
    NORMAL("normal"), RAW("raw");

    private final String key;

    TrainDiagramType(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public static TrainDiagramType getByKey(String key) {
        for (TrainDiagramType type : values()) {
            if (type.getKey().equals(key))
                return type;
        }
        return null;
    }
}
