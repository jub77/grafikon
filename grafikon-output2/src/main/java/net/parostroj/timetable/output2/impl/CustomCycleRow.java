package net.parostroj.timetable.output2.impl;

import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.parostroj.timetable.model.TrainsCycleItem;
import net.parostroj.timetable.model.TranslatedString;

/**
 * Custom cycle row.
 *
 * @author jub
 */
@XmlType(propOrder={"trainName", "fromTime", "fromAbbr", "toTime", "toAbbr", "wait"})
public class CustomCycleRow {

    private TranslatedString trainName;
    private String fromTime;
    private String fromAbbr;
    private String toTime;
    private String toAbbr;
    private Integer wait;
    private TrainsCycleItem ref;

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

    @XmlJavaTypeAdapter(TStringAdapter.class)
    public TranslatedString getTrainName() {
        return trainName;
    }

    public void setTrainName(TranslatedString trainName) {
        this.trainName = trainName;
    }

    public Integer getWait() {
        return wait;
    }

    public void setWait(Integer wait) {
        this.wait = wait;
    }

    public String getToTime() {
        return toTime;
    }

    public void setToTime(String toTime) {
        this.toTime = toTime;
    }

    @XmlTransient
    public TrainsCycleItem getRef() {
        return ref;
    }

    public void setRef(TrainsCycleItem ref) {
        this.ref = ref;
    }
}
