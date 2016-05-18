package net.parostroj.timetable.output2.impl;

import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.parostroj.timetable.model.LocalizedString;

/**
 * Information about custom cycle for train unit output.
 *
 * @author jub
 */
@XmlType(propOrder = { "type", "name", "fromAbbr", "toAbbr" })
public class TrainUnitCustomCycle {

    private LocalizedString type;
    private String name;
    private String fromAbbr;
    private String toAbbr;

    public TrainUnitCustomCycle() {
    }

    public TrainUnitCustomCycle(LocalizedString type, String name, String fromAbbr, String toAbbr) {
        super();
        this.type = type;
        this.name = name;
        this.fromAbbr = fromAbbr;
        this.toAbbr = toAbbr;
    }

    @XmlJavaTypeAdapter(type = LString.class, value = LStringAdapter.class)
    public LocalizedString getType() {
        return type;
    }

    public void setType(LocalizedString type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFromAbbr() {
        return fromAbbr;
    }

    public void setFromAbbr(String fromAbbr) {
        this.fromAbbr = fromAbbr;
    }

    public String getToAbbr() {
        return toAbbr;
    }

    public void setToAbbr(String toAbbr) {
        this.toAbbr = toAbbr;
    }
}
