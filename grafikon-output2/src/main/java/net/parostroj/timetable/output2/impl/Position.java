package net.parostroj.timetable.output2.impl;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.parostroj.timetable.model.TranslatedString;

/**
 * Start/End positions information.
 *
 * @author jub
 */
@XmlType(propOrder = {"cycleName", "cycleDescription", "stationName", "track", "time", "trainName", "attributes"})
public class Position {

    private String cycleName;
    private String cycleDescription;
    private String stationName;
    private TranslatedString trainName;
    private String track;
    private String time;
    private List<Attribute> attributes;

    public Position() {
    }

    public Position(String cycleName, String cycleDescription, String stationName, String track, String time, TranslatedString trainName) {
        this.cycleName = cycleName;
        this.cycleDescription = cycleDescription;
        this.stationName = stationName;
        this.trainName = trainName;
        this.track = track;
        this.time = time;
    }

    public Position(String cycleName, String cycleDescription, String stationName, String track, String time, TranslatedString trainName, List<Attribute> attributes) {
        this(cycleName, cycleDescription, stationName, track, time, trainName);
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

    public TranslatedString getTrainName() {
        return trainName;
    }

    @XmlJavaTypeAdapter(TStringAdapter.class)
    public void setTrainName(TranslatedString trainName) {
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
