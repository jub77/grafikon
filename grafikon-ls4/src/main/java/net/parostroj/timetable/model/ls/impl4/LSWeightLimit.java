package net.parostroj.timetable.model.ls.impl4;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Storage for weight info.
 *
 * @author jub
 */
@XmlType(propOrder = {"lineClass", "weight"})
public class LSWeightLimit {

    private String lineClass;
    private int weight;

    public LSWeightLimit() {
    }

    public LSWeightLimit(String lineClass, int weight) {
        this.lineClass = lineClass;
        this.weight = weight;
    }

    @XmlElement(name = "line_class")
    public String getLineClass() {
        return lineClass;
    }

    public void setLineClass(String lineClass) {
        this.lineClass = lineClass;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
