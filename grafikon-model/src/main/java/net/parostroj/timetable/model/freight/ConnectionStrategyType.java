package net.parostroj.timetable.model.freight;

/**
 * Type of strategy.
 *
 * @author jub
 */
public enum ConnectionStrategyType {
    BASE("base"), REGION("region"), CUSTOM_CONNECTION_FILTER("custom.connection.filter");

    private final String key;

    private ConnectionStrategyType(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public static ConnectionStrategyType fromString(String key) {
        for (ConnectionStrategyType type : values()) {
            if (type.key.equals(key)) {
                return type;
            }
        }
        return null;
    }
}