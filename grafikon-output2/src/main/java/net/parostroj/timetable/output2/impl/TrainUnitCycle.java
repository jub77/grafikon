package net.parostroj.timetable.output2.impl;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.*;

/**
 * Train unit cycle.
 *
 * @author jub
 */
@XmlType(propOrder = {"id", "name", "description", "attributes", "rows", "nextInSequence"})
public class TrainUnitCycle {

    private String id;
    private String name;
    private String description;
    private List<Attribute> attributes;
    private List<TrainUnitCycleRow> rows;
    private List<TrainUnitCycle> nextInSequence;

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

    @XmlElement(name = "row")
    public List<TrainUnitCycleRow> getRows() {
        if (rows == null)
            rows = new LinkedList<TrainUnitCycleRow>();
        return rows;
    }

    public void setRows(List<TrainUnitCycleRow> rows) {
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
    public List<TrainUnitCycle> getNextInSequence() {
        return nextInSequence;
    }

    public void setNextInSequence(List<TrainUnitCycle> nextInSequence) {
        this.nextInSequence = nextInSequence;
    }
}
