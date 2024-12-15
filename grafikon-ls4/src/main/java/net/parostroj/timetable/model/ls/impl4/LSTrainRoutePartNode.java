package net.parostroj.timetable.model.ls.impl4;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import net.parostroj.timetable.model.TimeInterval;

import java.util.Objects;

/**
 * Route part for node.
 *
 * @author jub
 */
@XmlType(propOrder = {"intervalId", "nodeId", "trackId", "stop", "attributes"})
public class LSTrainRoutePartNode {

    private String intervalId;
    private String nodeId;
    private String trackId;
    private int stop;
    private LSAttributes attributes;

    public LSTrainRoutePartNode() {
    }

    public LSTrainRoutePartNode(TimeInterval interval) {
        intervalId = interval.getId();
        nodeId = interval.getOwner().getId();
        trackId = interval.getTrack().getId();
        stop = interval.getLength();
        this.attributes = new LSAttributes(interval.getAttributes());
    }

    @XmlElement(name = "node_id")
    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public int getStop() {
        return stop;
    }

    public void setStop(int stop) {
        this.stop = stop;
    }

    @XmlElement(name = "track_id")
    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    @XmlElement(name = "interval_id")
    public String getIntervalId() {
        return intervalId;
    }

    public void setIntervalId(String intervalId) {
        this.intervalId = intervalId;
    }

    public LSAttributes getAttributes() {
        return Objects.requireNonNullElseGet(attributes, LSAttributes::new);
    }

    public void setAttributes(LSAttributes attributes) {
        this.attributes = attributes;
    }
}
