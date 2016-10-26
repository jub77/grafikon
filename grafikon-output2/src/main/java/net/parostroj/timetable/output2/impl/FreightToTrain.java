package net.parostroj.timetable.output2.impl;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.parostroj.timetable.model.TranslatedString;

/**
 * Freight passed to train (station timetables).
 *
 * @author jub
 */
@XmlType(propOrder = {"freightTo"})
public class FreightToTrain {

    private TranslatedString train;
    private List<FreightDstInfo> freightTo;

    public FreightToTrain() {
    }

    public List<FreightDstInfo> getFreightTo() {
        return freightTo;
    }

    public void setFreightTo(List<FreightDstInfo> freightTo) {
        this.freightTo = freightTo;
    }

    @XmlJavaTypeAdapter(TStringAdapter.class)
    public TranslatedString getTrain() {
        return train;
    }

    @XmlAttribute
    public void setTrain(TranslatedString train) {
        this.train = train;
    }
}
