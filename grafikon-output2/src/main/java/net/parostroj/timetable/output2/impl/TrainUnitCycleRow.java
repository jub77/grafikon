package net.parostroj.timetable.output2.impl;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlType;

/**
 * Train unit cycle row.
 *
 * @author jub
 */
@XmlType(propOrder={"trainName", "fromTime", "toTime", "fromAbbr", "toAbbr", "comment", "cycle"})
public class TrainUnitCycleRow {

    private String trainName;
    private String fromTime;
    private String fromAbbr;
    private String toTime;
    private String toAbbr;
    private String comment;
    private List<TrainUnitCustomCycle> cycle;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getFromAbbr() {
        return fromAbbr;
    }

    public void setFromAbbr(String fromAbbr) {
        this.fromAbbr = fromAbbr;
    }

    public String getFromTime() {
        return fromTime;
    }

    public void setFromTime(String fromTime) {
        this.fromTime = fromTime;
    }

    public String getToAbbr() {
        return toAbbr;
    }

    public void setToAbbr(String toAbbr) {
        this.toAbbr = toAbbr;
    }

    public String getTrainName() {
        return trainName;
    }

    public void setTrainName(String trainName) {
        this.trainName = trainName;
    }

    public String getToTime() {
        return toTime;
    }

    public void setToTime(String toTime) {
        this.toTime = toTime;
    }

    public List<TrainUnitCustomCycle> getCycle() {
        if (cycle == null)
            cycle = new LinkedList<TrainUnitCustomCycle>();
        return cycle;
    }

    public void setCycle(List<TrainUnitCustomCycle> cycle) {
        this.cycle = cycle;
    }
}
