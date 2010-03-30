package net.parostroj.timetable.output2.impl;

import javax.xml.bind.annotation.XmlType;

/**
 * Engine to.
 *
 * @author jub
 */
@XmlType(propOrder = {"cycleName", "trainName", "time"})
class EngineTo {
    private String cycleName;
    private String trainName;
    private String time;

    public EngineTo() {
    }

    public EngineTo(String cycleName, String trainName, String time) {
        this.cycleName = cycleName;
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
}
