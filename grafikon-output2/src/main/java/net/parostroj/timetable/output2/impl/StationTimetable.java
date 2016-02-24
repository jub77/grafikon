package net.parostroj.timetable.output2.impl;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import net.parostroj.timetable.model.NodeType;

/**
 * Station timetable.
 *
 * @author jub
 */
@XmlType(propOrder = {"name", "type", "regions", "company", "rows"})
public class StationTimetable {

    private String name;
    private NodeType type;
    private List<RegionInfo> regions;
    private CompanyInfo company;
    private List<StationTimetableRow> rows;

    public StationTimetable() {
    }

    public StationTimetable(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public NodeType getType() {
        return type;
    }

    public void setType(NodeType type) {
        this.type = type;
    }

    public List<RegionInfo> getRegions() {
        return regions;
    }

    public void setRegions(List<RegionInfo> regions) {
        this.regions = regions;
    }

    public CompanyInfo getCompany() {
        return company;
    }

    public void setCompany(CompanyInfo company) {
        this.company = company;
    }

    @XmlElement(name = "row")
    public List<StationTimetableRow> getRows() {
        if (rows == null) {
            rows = new LinkedList<StationTimetableRow>();
        }
        return rows;
    }

    public void setRows(List<StationTimetableRow> rows) {
        this.rows = rows;
    }
}
