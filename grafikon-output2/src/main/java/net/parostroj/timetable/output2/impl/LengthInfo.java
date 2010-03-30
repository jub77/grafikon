package net.parostroj.timetable.output2.impl;

import javax.xml.bind.annotation.XmlType;

/**
 * Length info for station row.
 *
 * @author jub
 */
@XmlType(propOrder = {"length", "lengthInAxles", "lengthUnit", "stationAbbr"})
public class LengthInfo {

    private int length;
    private boolean lengthInAxles;
    private String lengthUnit;
    private String stationAbbr;

    public LengthInfo() {
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

    public String getStationAbbr() {
        return stationAbbr;
    }

    public void setStationAbbr(String stationAbbr) {
        this.stationAbbr = stationAbbr;
    }
}
