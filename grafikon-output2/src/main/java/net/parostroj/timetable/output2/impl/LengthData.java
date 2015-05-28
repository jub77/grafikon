package net.parostroj.timetable.output2.impl;

import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.parostroj.timetable.model.units.LengthUnit;

/**
 * Length info for station row.
 *
 * @author jub
 */
@XmlType(propOrder = {"length", "lengthInAxles", "lengthUnit"})
public class LengthData {

    private int length;
    private boolean lengthInAxles;
    private LengthUnit lengthUnit;

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

    @XmlJavaTypeAdapter(LengthUnitAdapter.class)
    public LengthUnit getLengthUnit() {
        return lengthUnit;
    }

    public void setLengthUnit(LengthUnit lengthUnit) {
        this.lengthUnit = lengthUnit;
    }
}
