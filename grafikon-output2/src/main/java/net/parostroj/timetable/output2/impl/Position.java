package net.parostroj.timetable.output2.impl;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Start/End positions information.
 *
 * @author jub
 */
@XmlType(propOrder = {"cycleName", "cycleDescription", "stationName", "track", "trainName", "attributes"})
public class Position {

    private String cycleName;
    private String cycleDescription;
    private String stationName;
    private String trainName;
    private String track;
    private List<Attribute> attributes;

    public Position() {
    }

    public Position(String cycleName, String cycleDescription, String stationName, String track, String trainName) {
        this.cycleName = cycleName;
        this.cycleDescription = cycleDescription;
        this.stationName = stationName;
        this.trainName = trainName;
        this.track = track;
    }

    public Position(String cycleName, String cycleDescription, String stationName, String track, String trainName, List<Attribute> attributes) {
        this(cycleName, cycleDescription, stationName, track, trainName);
        this.attributes = attributes;
    }

    public String getCycleDescription() {
        return cycleDescription;
    }

    public void setCycleDescription(String cycleDescription) {
        this.cycleDescription = cycleDescription;
    }

    public String getCycleName() {
        return cycleName;
    }

    public void setCycleName(String cycleName) {
        this.cycleName = cycleName;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getTrainName() {
        return trainName;
    }

    public void setTrainName(String trainName) {
        this.trainName = trainName;
    }

    @XmlElement(name = "attribute")
    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }
    
    public String getTrack() {
        return track;
    }
    
    public void setTrack(String track) {
        this.track = track;
    }
}
