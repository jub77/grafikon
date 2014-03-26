package net.parostroj.timetable.model.ls.impl4;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import net.parostroj.timetable.model.TimeInterval;

/**
 * Route part for line.
 *
 * @author jub
 */
@XmlType(propOrder = {"intervalId", "lineId", "trackId", "speed", "addedTime", "attributes"})
public class LSTrainRoutePartLine {

    private String intervalId;
    private String lineId;
    private String trackId;
    private Integer speed;
    private Integer addedTime;
    private LSAttributes attributes;

    public LSTrainRoutePartLine() {
    }

    public LSTrainRoutePartLine(TimeInterval interval) {
        intervalId = interval.getId();
        lineId = interval.getOwner().getId();
        trackId = interval.getTrack().getId();
        speed = interval.getSpeedLimit();
        if (interval.getAddedTime() != 0)
            addedTime = interval.getAddedTime();
        this.attributes = new LSAttributes(interval.getAttributes());
    }

    @XmlElement(name = "line_id")
    public String getLineId() {
        return lineId;
    }

    public void setLineId(String lineId) {
        this.lineId = lineId;
    }

    public Integer getSpeed() {
        return speed;
    }

    public void setSpeed(Integer speed) {
        this.speed = speed;
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

    @XmlElement(name = "added_time")
    public Integer getAddedTime() {
        return addedTime;
    }

    public void setAddedTime(Integer addedTime) {
        this.addedTime = addedTime;
    }

    public LSAttributes getAttributes() {
        if (attributes == null)
            return new LSAttributes();
        else
            return attributes;
    }

    public void setAttributes(LSAttributes attributes) {
        this.attributes = attributes;
    }
}
