package net.parostroj.timetable.output2.impl;

import java.util.LinkedList;
import java.util.List;

import jakarta.xml.bind.annotation.*;

import net.parostroj.timetable.model.TrainsCycle;

/**
 * Engine cycle.
 *
 * @author jub
 */
@XmlType(propOrder = {"id", "name", "description", "attributes", "rows", "company"})
public class EngineCycle {

    private String id;
    private String name;
    private String description;
    private List<Attribute> attributes;
    private List<EngineCycleRow> rows;
    private EngineCycle next;
    private CompanyInfo company;
    private TrainsCycle ref;

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
    public List<EngineCycleRow> getRows() {
        if (rows == null)
            rows = new LinkedList<>();
        return rows;
    }

    public void setRows(List<EngineCycleRow> rows) {
        this.rows = rows;
    }

    @XmlElement(name = "attribute")
    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    @XmlIDREF
    @XmlAttribute
    public EngineCycle getNext() {
        return next;
    }

    public void setNext(EngineCycle next) {
        this.next = next;
    }

    public CompanyInfo getCompany() {
        return company;
    }

    public void setCompany(CompanyInfo company) {
        this.company = company;
    }

    @XmlTransient
    public TrainsCycle getRef() {
        return ref;
    }

    public void setRef(TrainsCycle ref) {
        this.ref = ref;
    }
}
