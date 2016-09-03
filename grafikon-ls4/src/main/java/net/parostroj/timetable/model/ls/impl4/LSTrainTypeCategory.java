package net.parostroj.timetable.model.ls.impl4;

import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import net.parostroj.timetable.model.PenaltyTableRow;
import net.parostroj.timetable.model.TrainTypeCategory;

/**
 * Storage for train type category.
 *
 * @author jub
 */
@XmlRootElement(name = "train_type_category")
@XmlType(propOrder = {"id", "name", "key", "rows"})
public class LSTrainTypeCategory {

    private String id;
    private String name;
    private String key;
    private List<LSPenaltyTableRow> rows;

    public LSTrainTypeCategory() {
    }

    public LSTrainTypeCategory(TrainTypeCategory category) {
        this.id = category.getId();
        this.name = category.getName();
        this.key = category.getKey();
        this.rows = new LinkedList<>();
        for (PenaltyTableRow r : category.getPenaltyRows()) {
            rows.add(new LSPenaltyTableRow(r));
        }
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

    public TrainTypeCategory createTrainTypeCategory() {
        TrainTypeCategory category = new TrainTypeCategory(id, name, key);
        if (getRows() != null)
            for (LSPenaltyTableRow lsRow : getRows()) {
                category.addRow(lsRow.createPenaltyTableRow());
            }
        return category;
    }
}
