package net.parostroj.timetable.output2.impl;

import javax.xml.bind.annotation.XmlType;

/**
 * Train unit to.
 *
 * @author jub
 */
@XmlType(propOrder = {"cycleName", "cycleDescription", "trainName", "time"})
public class TrainUnitTo {
    private String cycleName;
    private String cycleDescription;
    private String trainName;
    private String time;

    public TrainUnitTo() {
    }

    public TrainUnitTo(String cycleName, String cycleDescription, String trainName, String time) {
        this.cycleName = cycleName;
        this.cycleDescription = cycleDescription;
        this.trainName = trainName;
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTrainName() {
        return trainName;
    }

    public void setTrainName(String trainName) {
        this.trainName = trainName;
    }

    public String getCycleName() {
        return cycleName;
    }

    public void setCycleName(String cycleName) {
        this.cycleName = cycleName;
    }

    public String getCycleDescription() {
        return cycleDescription;
    }

    public void setCycleDescription(String cycleDescription) {
        this.cycleDescription = cycleDescription;
    }
}
