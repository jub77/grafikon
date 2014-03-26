package net.parostroj.timetable.model.ls.impl3;

import javax.xml.bind.annotation.XmlType;
import net.parostroj.timetable.model.TimeInterval;

/**
 * Route part for line.
 * 
 * @author jub
 */
@XmlType(propOrder = {"intervalId", "lineId", "trackId", "speed", "attributes"})
public class LSTrainRoutePartLine {

    private String intervalId;
    private String lineId;
    private String trackId;
    private int speed;
    private LSAttributes attributes;

    public LSTrainRoutePartLine() {
    }

    public LSTrainRoutePartLine(TimeInterval interval) {
        intervalId = interval.getId();
        lineId = interval.getOwner().getId();
        trackId = interval.getTrack().getId();
        speed = interval.getSpeedLimit();
        this.attributes = new LSAttributes(interval.getAttributes());
    }

    public String getLineId() {
        return lineId;
    }

    public void setLineId(String lineId) {
        this.lineId = lineId;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public String getIntervalId() {
        return intervalId;
    }

    public void setIntervalId(String intervalId) {
        this.intervalId = intervalId;
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
