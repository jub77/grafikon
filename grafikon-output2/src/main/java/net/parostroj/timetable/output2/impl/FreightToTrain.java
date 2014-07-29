package net.parostroj.timetable.output2.impl;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * Freight passed to train (station timetables).
 *
 * @author jub
 */
@XmlType(propOrder = {"freightTo"})
public class FreightToTrain {

    private String train;
    private List<String> freightTo;

    public FreightToTrain() {
    }

    public List<String> getFreightTo() {
        return freightTo;
    }

    public void setFreightTo(List<String> freightTo) {
        this.freightTo = freightTo;
    }

    public String getTrain() {
        return train;
    }

    @XmlAttribute
    public void setTrain(String train) {
        this.train = train;
    }
}
