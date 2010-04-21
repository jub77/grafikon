package net.parostroj.timetable.output2.impl;

import javax.xml.bind.annotation.XmlType;

/**
 * Weight data row.
 *
 * @author jub
 */
@XmlType(propOrder={"engine", "from", "to", "weight"})
public class WeightDataRow {
    private String engine;
    private String from;
    private String to;
    private String weight;

    public WeightDataRow() {
    }

    public WeightDataRow(String engine, String from, String to, String weight) {
        this.engine = engine;
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    public String getEngine() {
        return engine;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getWeight() {
        return weight;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }
}
