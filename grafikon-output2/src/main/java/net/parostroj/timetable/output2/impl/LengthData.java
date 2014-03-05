package net.parostroj.timetable.output2.impl;

import javax.xml.bind.annotation.XmlType;

/**
 * Length info for station row.
 *
 * @author jub
 */
@XmlType(propOrder = {"length", "lengthInAxles", "lengthUnit"})
public class LengthData {

    private int length;
    private boolean lengthInAxles;
    private String lengthUnit;

    public LengthData() {
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public boolean isLengthInAxles() {
        return lengthInAxles;
    }

    public void setLengthInAxles(boolean lengthInAxles) {
        this.lengthInAxles = lengthInAxles;
    }

    public String getLengthUnit() {
        return lengthUnit;
    }

    public void setLengthUnit(String lengthUnit) {
        this.lengthUnit = lengthUnit;
    }
}
