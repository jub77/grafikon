package net.parostroj.timetable.output2.impl;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Weight data row.
 *
 * @author jub
 */
@XmlType(propOrder={"engines", "from", "to", "weight"})
public class WeightDataRow {
    private List<String> engines;
    private String from;
    private String to;
    private Integer weight;

    public WeightDataRow() {
    }

    public WeightDataRow(List<String> engines, String from, String to, Integer weight) {
        this.engines = engines;
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    @XmlElement(name="engine")
    public List<String> getEngines() {
        return engines;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setEngines(List<String> engines) {
        this.engines = engines;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public boolean isRowEmpty() {
        return weight == null && from == null && to == null && (engines == null || engines.isEmpty());
    }
}
