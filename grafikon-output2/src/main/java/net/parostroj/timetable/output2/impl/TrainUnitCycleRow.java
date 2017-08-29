package net.parostroj.timetable.output2.impl;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.parostroj.timetable.model.LocalizedString;
import net.parostroj.timetable.model.TrainsCycleItem;
import net.parostroj.timetable.model.TranslatedString;

/**
 * Train unit cycle row.
 *
 * @author jub
 */
@XmlType(propOrder={"trainName", "fromTime", "toTime", "fromAbbr", "toAbbr", "comment", "cycle"})
public class TrainUnitCycleRow {

    private TranslatedString trainName;
    private String fromTime;
    private String fromAbbr;
    private String toTime;
    private String toAbbr;
    private LocalizedString comment;
    private List<TrainUnitCustomCycle> cycle;
    private TrainsCycleItem ref;

    @XmlJavaTypeAdapter(type = LString.class, value = LStringAdapter.class)
    public LocalizedString getComment() {
        return comment;
    }

    public void setComment(LocalizedString comment) {
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

    @XmlJavaTypeAdapter(TStringAdapter.class)
    public TranslatedString getTrainName() {
        return trainName;
    }

    public void setTrainName(TranslatedString trainName) {
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
            cycle = new LinkedList<>();
        return cycle;
    }

    public void setCycle(List<TrainUnitCustomCycle> cycle) {
        this.cycle = cycle;
    }

    @XmlTransient
    public TrainsCycleItem getRef() {
        return ref;
    }

    public void setRef(TrainsCycleItem ref) {
        this.ref = ref;
    }
}
