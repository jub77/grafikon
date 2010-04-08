package net.parostroj.timetable.output2.impl;

import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

/**
 * Train unit cycle card.
 *
 * @author jub
 */
@XmlType(propOrder = {"name", "description", "rows"})
public class TrainUnitCycleCard {

    private String name;
    private String description;
    private List<TrainUnitCycleCardRow> rows;

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

    @XmlElementWrapper
    @XmlElement(name = "row")
    public List<TrainUnitCycleCardRow> getRows() {
        if (rows == null)
            rows = new LinkedList<TrainUnitCycleCardRow>();
        return rows;
    }

    public void setRows(List<TrainUnitCycleCardRow> rows) {
        this.rows = rows;
    }
}
