package net.parostroj.timetable.output2.impl;

import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Station timetable.
 *
 * @author jub
 */
@XmlType(propOrder = {"name", "rows"})
public class StationTimetable {

    private String name;
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
