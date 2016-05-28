package net.parostroj.timetable.model.ls.impl4;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import net.parostroj.timetable.model.TrainTypeCategory;

/**
 * Storage for penalty table.
 *
 * @author jub
 */
@XmlRootElement(name = "penalty_table")
@XmlType(propOrder = { "categories" })
public class LSPenaltyTable {

    private List<LSTrainTypeCategory> categories;

    public LSPenaltyTable() {
    }

    public LSPenaltyTable(List<TrainTypeCategory> categoryList) {
        this.categories = new LinkedList<LSTrainTypeCategory>();
        for (TrainTypeCategory category : categoryList) {
            this.categories.add(new LSTrainTypeCategory(category));
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

    public List<TrainTypeCategory> createPenaltyTable() {
        List<TrainTypeCategory> table = new ArrayList<>();
        if (this.categories != null) {
            for (LSTrainTypeCategory lsCategory : this.categories) {
                TrainTypeCategory category = lsCategory.createTrainTypeCategory();
                table.add(category);
                if (lsCategory.getRows() != null)
                    for (LSPenaltyTableRow lsRow : lsCategory.getRows()) {
                        category.addRow(lsRow.createPenaltyTableRow());
                    }
            }
        }
        return table;
    }
}
