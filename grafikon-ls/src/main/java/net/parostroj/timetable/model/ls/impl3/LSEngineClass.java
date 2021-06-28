package net.parostroj.timetable.model.ls.impl3;

import java.util.LinkedList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import net.parostroj.timetable.model.EngineClass;
import net.parostroj.timetable.model.Net;
import net.parostroj.timetable.model.WeightTableRow;

/**
 * Storage for engine class.
 *
 * @author jub
 */
@XmlRootElement(name = "engine_class")
@XmlType(propOrder = {"id", "name", "rows"})
public class LSEngineClass {

    private String id;
    private String name;
    private List<LSWeightTableRow> rows;

    public LSEngineClass() {
    }

    public LSEngineClass(EngineClass engineClass) {
        this.id = engineClass.getId();
        this.name = engineClass.getName();
        this.rows = new LinkedList<>();
        for (WeightTableRow row : engineClass.getWeightTable()) {
            this.rows.add(new LSWeightTableRow(row));
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElementWrapper(name = "weight_table")
    @XmlElement(name = "row")
    public List<LSWeightTableRow> getRows() {
        return rows;
    }

    public void setRows(List<LSWeightTableRow> rows) {
        this.rows = rows;
    }

    public EngineClass createEngineClass(Net net) {
        EngineClass ec = new EngineClass(id);
        ec.setName(name);
        for (LSWeightTableRow lsRow : rows) {
            ec.addWeightTableRow(lsRow.createWeightTableRow(net, ec));
        }
        return ec;
    }
}
