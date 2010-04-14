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
@XmlType(propOrder={"name", "description", "rows"})
public class DriverCycle {

    private String name;
    private String description;
    private List<DriverCycleRow> rows;

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
}
