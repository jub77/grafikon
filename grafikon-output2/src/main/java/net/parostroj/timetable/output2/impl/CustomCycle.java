package net.parostroj.timetable.output2.impl;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.*;

/**
 * Custom cycle.
 *
 * @author jub
 */
@XmlType(propOrder = {"id", "name", "description", "type", "attributes", "rows", "nextInSequence"})
public class CustomCycle {

    private String id;
    private String name;
    private String description;
    private String type;
    private List<Attribute> attributes;
    private List<CustomCycleRow> rows;
    private List<CustomCycle> nextInSequence;

    @XmlID
    @XmlAttribute
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @XmlElement(name = "row")
    public List<CustomCycleRow> getRows() {
        if (rows == null)
            rows = new LinkedList<CustomCycleRow>();
        return rows;
    }

    public void setRows(List<CustomCycleRow> rows) {
        this.rows = rows;
    }

    @XmlElement(name = "attribute")
    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    @XmlElement(name = "next")
    @XmlIDREF
    public List<CustomCycle> getNextInSequence() {
        return nextInSequence;
    }

    public void setNextInSequence(List<CustomCycle> nextInSequence) {
        this.nextInSequence = nextInSequence;
    }
}
