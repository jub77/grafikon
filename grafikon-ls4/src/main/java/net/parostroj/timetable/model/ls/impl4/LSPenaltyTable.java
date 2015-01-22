package net.parostroj.timetable.model.ls.impl4;

import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import net.parostroj.timetable.model.PenaltyTable;
import net.parostroj.timetable.model.TrainTypeCategory;

/**
 * Storage for penalty table.
 *
 * @author jub
 */
@XmlRootElement(name = "penalty_table")
@XmlType(propOrder= {"id", "categories"})
public class LSPenaltyTable {

    private String id;
    private List<LSTrainTypeCategory> categories;

    public LSPenaltyTable() {
    }

    public LSPenaltyTable(PenaltyTable table) {
        this.id = table.getId();
        this.categories = new LinkedList<LSTrainTypeCategory>();
        for (TrainTypeCategory category : table.getTrainTypeCategories()) {
            this.categories.add(new LSTrainTypeCategory(category, table.getPenaltyTableRowsForCategory(category)));
        }
    }

    @XmlElementWrapper(name = "categories")
    @XmlElement(name = "category")
    public List<LSTrainTypeCategory> getCategories() {
        return categories;
    }

    public void setCategories(List<LSTrainTypeCategory> categories) {
        this.categories = categories;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PenaltyTable createPenaltyTable() {
        PenaltyTable table = new PenaltyTable(id);
        if (this.categories != null) {
            for (LSTrainTypeCategory lsCategory : this.categories) {
                TrainTypeCategory category = lsCategory.createTrainTypeCategory();
                table.addTrainTypeCategory(category);
                if (lsCategory.getRows() != null)
                    for (LSPenaltyTableRow lsRow : lsCategory.getRows()) {
                        table.addRowForCategory(category, lsRow.createPenaltyTableRow());
                    }
            }
        }
        return table;
    }
}
