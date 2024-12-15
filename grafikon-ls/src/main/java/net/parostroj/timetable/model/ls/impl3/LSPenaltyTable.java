package net.parostroj.timetable.model.ls.impl3;

import java.util.List;

/**
 * Penalty table.
 *
 * @author jub
 */
public class LSPenaltyTable {

    private List<LSPenaltyTableItem> itemList;

    /**
     * Default constructor.
     */
    public LSPenaltyTable() {
    }

    /**
     * @return the table
     */
    public List<LSPenaltyTableItem> getItemList() {
        return itemList;
    }

    /**
     * @param table the table to set
     */
    public void setItemList(List<LSPenaltyTableItem> table) {
        this.itemList = table;
    }

    @Override
    public String toString() {
        return itemList.toString();
    }
}
