package net.parostroj.timetable.output2.impl;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.parostroj.timetable.model.LocalizedString;
import net.parostroj.timetable.model.TrainsCycle;

/**
 * Custom cycle.
 *
 * @author jub
 */
@XmlType(propOrder = {"id", "name", "description", "typeKey", "typeName", "attributes", "rows", "company"})
public class CustomCycle {

    private String id;
    private String name;
    private String description;
    private String typeKey;
    private LocalizedString typeName;
    private List<Attribute> attributes;
    private List<CustomCycleRow> rows;
    private CustomCycle next;
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

    public void setTypeKey(String typeKey) {
        this.typeKey = typeKey;
    }

    public String getTypeKey() {
        return typeKey;
    }

    public void setTypeName(LocalizedString typeName) {
        this.typeName = typeName;
    }

    @XmlJavaTypeAdapter(type = LString.class, value = LStringAdapter.class)
    public LocalizedString getTypeName() {
        return typeName;
    }

    @XmlElement(name = "row")
    public List<CustomCycleRow> getRows() {
        if (rows == null)
            rows = new LinkedList<>();
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

    @XmlIDREF
    @XmlAttribute
    public CustomCycle getNext() {
        return next;
    }

    public void setNext(CustomCycle next) {
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
