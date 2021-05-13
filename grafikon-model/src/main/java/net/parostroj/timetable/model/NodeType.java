/*
 * NodeType.java
 *
 * Created on 29.8.2007, 22:10:27
 */
package net.parostroj.timetable.model;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Type of the node.
 *
 * @author jub
 */
public enum NodeType {

    STATION("station"),
    ROUTE_SPLIT("route.split"),
    STATION_HIDDEN("hidden.station"),
    STATION_FREIGHT("freight.station"),
    STATION_BRANCH("branch.station"),
    STOP("stop"),
    STOP_WITH_FREIGHT("stop.with.freight"),
    SIGNAL("signal");
    private final String key;

    NodeType(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public static NodeType fromKey(String key) {
        for (NodeType type : values()) {
            if (type.getKey().equals(key)) {
                return type;
            }
        }
        return null;
    }

    public static NodeType fromString(String id) {
        for (NodeType type : values()) {
            if (type.toString().equals(id)) {
                return type;
            }
        }
        return null;
    }

    public static Set<NodeType> filteredOf(Predicate<NodeType> predicate) {
        return Stream.of(values())
                .filter(predicate)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(NodeType.class)));
    }

    public boolean isStation() {
        return this == STATION || this == STATION_BRANCH || this == STATION_FREIGHT || this == STATION_HIDDEN;
    }

    public boolean isStop() {
        return this == STOP || this == STOP_WITH_FREIGHT;
    }

    public boolean isPassenger() {
        return this == STATION || this == STATION_BRANCH || this == STOP;
    }

    public boolean isFreight() {
        return this == STATION || this == STATION_BRANCH || this == STATION_FREIGHT || this == STATION_HIDDEN
                || this == STOP_WITH_FREIGHT;
    }

    public boolean isHidden() {
        return this == STATION_HIDDEN;
    }
}
