package net.parostroj.timetable.output2.impl;

import javax.xml.bind.annotation.XmlType;

/**
 * Start/End positions information.
 *
 * @author jub
 */
@XmlType(propOrder = {"cycleName", "cycleDescription", "stationName", "trainName"})
public class Position {

    private String cycleName;
    private String cycleDescription;
    private String stationName;
    private String trainName;

    public Position() {
    }

    public Position(String cycleName, String cycleDescription, String stationName, String trainName) {
        this.cycleName = cycleName;
        this.cycleDescription = cycleDescription;
        this.stationName = stationName;
        this.trainName = trainName;
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
}
