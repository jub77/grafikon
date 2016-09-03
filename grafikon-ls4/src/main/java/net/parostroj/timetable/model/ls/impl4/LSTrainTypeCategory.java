package net.parostroj.timetable.model.ls.impl4;

import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import net.parostroj.timetable.model.LocalizedString;
import net.parostroj.timetable.model.PenaltyTableRow;
import net.parostroj.timetable.model.TrainTypeCategory;
import net.parostroj.timetable.model.ls.LSException;

/**
 * Storage for train type category.
 *
 * @author jub
 */
@XmlRootElement(name = "train_type_category")
@XmlType(name = "train_type_category", propOrder = {"id", "name", "key", "rows", "attributes"})
public class LSTrainTypeCategory {

    private String id;
    private String name;
    private String key;
    private List<LSPenaltyTableRow> rows;
    private LSAttributes attributes;

    public LSTrainTypeCategory() {
    }

    public LSTrainTypeCategory(TrainTypeCategory category) {
        this.id = category.getId();
        this.rows = new LinkedList<>();
        for (PenaltyTableRow r : category.getPenaltyRows()) {
            rows.add(new LSPenaltyTableRow(r));
        }
        this.attributes = new LSAttributes(category.getAttributes());
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @XmlElementWrapper(name = "penalty_rows")
    @XmlElement(name = "row")
    public List<LSPenaltyTableRow> getRows() {
        return rows;
    }

    public void setRows(List<LSPenaltyTableRow> rows) {
        this.rows = rows;
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

    public LSAttributes getAttributes() {
        return attributes;
    }

    public void setAttributes(LSAttributes attributes) {
        this.attributes = attributes;
    }

    public TrainTypeCategory createTrainTypeCategory() throws LSException {
        TrainTypeCategory category = new TrainTypeCategory(id);
        if (getRows() != null)
            for (LSPenaltyTableRow lsRow : getRows()) {
                category.addRow(lsRow.createPenaltyTableRow(category));
            }
        if (name != null) {
            category.setName(LocalizedString.fromString(name));
        }
        if (key != null) {
            category.setKey(key);
        }
        if (attributes != null) {
            category.getAttributes().add(attributes.createAttributes());
        }
        return category;
    }
}
