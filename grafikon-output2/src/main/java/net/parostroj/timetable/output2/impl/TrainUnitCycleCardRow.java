package net.parostroj.timetable.output2.impl;

import javax.xml.bind.annotation.XmlType;

/**
 * Train unit cycle card row.
 *
 * @author jub
 */
@XmlType(propOrder={"trainName", "fromTime", "fromAbbr", "toAbbr", "comment"})
public class TrainUnitCycleCardRow {

    private String trainName;
    private String fromTime;
    private String fromAbbr;
    private String toAbbr;
    private String comment;

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
}
