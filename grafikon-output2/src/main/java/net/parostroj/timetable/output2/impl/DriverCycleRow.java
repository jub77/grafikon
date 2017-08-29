package net.parostroj.timetable.output2.impl;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.parostroj.timetable.model.LocalizedString;
import net.parostroj.timetable.model.TrainsCycleItem;
import net.parostroj.timetable.model.TranslatedString;

/**
 * Driver cycle row.
 *
 * @author jub
 */
@XmlType(propOrder = { "trainName", "fromTime", "fromAbbr", "from", "toTime", "toAbbr", "to", "comment", "setupTime",
        "technologicalTime" })
public class DriverCycleRow {

    private TranslatedString trainName;
    private String fromTime;
    private String fromAbbr;
    private String from;
    private String toTime;
    private String toAbbr;
    private String to;
    private LocalizedString comment;
    private Integer setupTime;
    private Integer technologicalTime;
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

    public String getToTime() {
        return toTime;
    }

    public void setToTime(String toTime) {
        this.toTime = toTime;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    @XmlJavaTypeAdapter(type = LString.class, value = LStringAdapter.class)
    public LocalizedString getComment() {
        return comment;
    }

    public void setComment(LocalizedString comment) {
        this.comment = comment;
    }

    public Integer getSetupTime() {
        return setupTime;
    }

    public void setSetupTime(Integer setupTime) {
        this.setupTime = setupTime;
    }

    public Integer getTechnologicalTime() {
        return technologicalTime;
    }

    public void setTechnologicalTime(Integer technologicalTime) {
        this.technologicalTime = technologicalTime;
    }

    @XmlTransient
    public TrainsCycleItem getRef() {
        return ref;
    }

    public void setRef(TrainsCycleItem ref) {
        this.ref = ref;
    }
}
