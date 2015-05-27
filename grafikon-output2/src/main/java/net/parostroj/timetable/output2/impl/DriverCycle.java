package net.parostroj.timetable.output2.impl;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Driver cycle.
 *
 * @author jub
 */
@XmlType(propOrder={"name", "description", "routes", "attributes", "rows", "company"})
public class DriverCycle {

    private String name;
    private String description;
    private List<NetPartRouteInfo> routes;
    private List<Attribute> attributes;
    private List<DriverCycleRow> rows;
    private CompanyInfo company;

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

    @XmlElement(name="row")
    public List<DriverCycleRow> getRows() {
        if (rows == null)
            rows = new LinkedList<DriverCycleRow>();
        return rows;
    }

    public void setRows(List<DriverCycleRow> rows) {
        this.rows = rows;
    }

    @XmlElement(name="route")
    public List<NetPartRouteInfo> getRoutes() {
        if (routes == null)
            routes = new LinkedList<NetPartRouteInfo>();
        return routes;
    }

    public void setRoutes(List<NetPartRouteInfo> routes) {
        this.routes = routes;
    }

    @XmlElement(name = "attribute")
    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    public CompanyInfo getCompany() {
        return company;
    }

    public void setCompany(CompanyInfo company) {
        this.company = company;
    }
}
