package net.parostroj.timetable.model.ls.impl4;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import net.parostroj.timetable.model.LineClass;

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
    
    public LSWeightLimit(LineClass lineClass, int weight) {
        this.lineClass = lineClass.getId();
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
