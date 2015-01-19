package net.parostroj.timetable.output2.impl;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * @author jub
 */
@XmlType(propOrder = {"name", "desc", "trainName", "time"})
public class CycleFromTo {

    private boolean in;
    private boolean start;
    private String name;
    private String desc;
    private String trainName;
    private String time;
    private Boolean helper;

    public CycleFromTo() {
    }

    public CycleFromTo(boolean start, boolean in, String name, String desc, String trainName, String time) {
        this.start = start;
        this.in = in;
        this.name = name;
        this.desc = desc;
        this.trainName = trainName;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTrainName() {
        return trainName;
    }

    public void setTrainName(String trainName) {
        this.trainName = trainName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @XmlAttribute
    public boolean isIn() {
        return in;
    }

    public void setIn(boolean in) {
        this.in = in;
    }

    @XmlAttribute
    public boolean isStart() {
        return start;
    }

    public void setStart(boolean start) {
        this.start = start;
    }

    @XmlAttribute
    public Boolean getHelper() {
        return helper;
    }

    public void setHelper(Boolean helper) {
        this.helper = helper;
    }
}
