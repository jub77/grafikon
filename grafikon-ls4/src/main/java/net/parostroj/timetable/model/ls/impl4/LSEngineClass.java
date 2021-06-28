package net.parostroj.timetable.model.ls.impl4;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import net.parostroj.timetable.model.EngineClass;
import net.parostroj.timetable.model.LineClass;
import net.parostroj.timetable.model.WeightTableRow;
import net.parostroj.timetable.model.ls.LSException;

/**
 * Storage for engine class.
 *
 * @author jub
 */
@XmlRootElement(name = "engine_class")
@XmlType(propOrder = {"id", "name", "attributes", "rows"})
public class LSEngineClass {

    private String id;
    // deprecated
    private String name;
    private LSAttributes attributes;
    private List<LSWeightTableRow> rows;

    private int version;

    public LSEngineClass() {
    }

    public LSEngineClass(EngineClass engineClass) {
        this.id = engineClass.getId();
        this.rows = new LinkedList<>();
        for (WeightTableRow row : engineClass.getWeightTable()) {
            this.rows.add(new LSWeightTableRow(row));
        }
        this.attributes = new LSAttributes(engineClass.getAttributes());
        this.version = 1;
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

    @XmlAttribute
    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @XmlElementWrapper(name = "weight_table")
    @XmlElement(name = "row")
    public List<LSWeightTableRow> getRows() {
        return rows;
    }

    public void setRows(List<LSWeightTableRow> rows) {
        this.rows = rows;
    }

    public LSAttributes getAttributes() {
        return attributes;
    }

    public void setAttributes(LSAttributes attributes) {
        this.attributes = attributes;
    }

    public EngineClass createEngineClass(Function<String, LineClass> lineClassMapping) throws LSException {
        EngineClass ec = new EngineClass(id);
        if (this.version == 0) {
            ec.setName(name);
        }
        if (this.rows != null) {
            for (LSWeightTableRow lsRow : this.rows) {
                ec.addWeightTableRow(lsRow.createWeightTableRow(lineClassMapping, ec));
            }
        }
        if (this.attributes != null) {
            ec.getAttributes().add(this.attributes.createAttributes());
        }
        return ec;
    }
}
