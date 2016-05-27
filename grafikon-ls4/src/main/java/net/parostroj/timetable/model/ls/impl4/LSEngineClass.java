package net.parostroj.timetable.model.ls.impl4;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import net.parostroj.timetable.model.EngineClass;
import net.parostroj.timetable.model.LineClass;
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
        this(engineClass, lineClass -> lineClass.getId());
    }

    public LSEngineClass(EngineClass engineClass, Function<LineClass, String> lineClassConvertor) {
        this.id = engineClass.getId();
        this.name = engineClass.getName();
        this.rows = new LinkedList<LSWeightTableRow>();
        for (WeightTableRow row : engineClass.getWeightTable()) {
            this.rows.add(new LSWeightTableRow(row, lineClassConvertor));
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
        return createEngineClass(lineClassId -> net.getLineClasses().getById(lineClassId));
    }

    public EngineClass createEngineClass(Function<String, LineClass> lineClassSource) {
        EngineClass ec = new EngineClass(id, name);
        if (this.rows != null) {
            for (LSWeightTableRow lsRow : this.rows) {
                ec.addWeightTableRow(lsRow.createWeightTableRow(lineClassSource, ec));
            }
        }
        return ec;
    }
}
