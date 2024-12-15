package net.parostroj.timetable.model.save;

import java.util.List;

/**
 * Penalty table.
 *
 * @author jub
 */
public class LSPenaltyTable {

    private static final double ADJUST_RATIO = 0.18d;
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

    /**
     * returns braking time penalty (in model seconds).
     *
     * @param type type
     * @param velocity velocity
     * @param timeScale timescale
     * @return braking time penalty
     */
    public int getBrakingTimePenalty(LSSBType type, int velocity, double timeScale) {
        LSPenaltyTableItem item = this.getItem(type, velocity);
        return this.adjustByRatio(item.getBrakingPenalty(), timeScale);
    }

    /**
     * returns speeding time penalty (in model seconds).
     *
     * @param type type
     * @param velocity velocity
     * @param timeScale timescale
     * @return speeding time penalty
     */
    public int getSpeedingTimePenalty(LSSBType type, int velocity, double timeScale) {
        LSPenaltyTableItem item = this.getItem(type, velocity);
        return this.adjustByRatio(item.getSpeedingPenalty(), timeScale);
    }

    private LSPenaltyTableItem getItem(LSSBType type, int velocity) {
        for (LSPenaltyTableItem item : itemList) {
            if (type == item.getType() && velocity >= item.getLowerLimit() && velocity < item.getUpperLimit()) {
                return item;
            }
        }
        throw new RuntimeException("Penalty not found.");
    }

    private int adjustByRatio(int penalty, double timeScale) {
        return (int) Math.round(penalty * ADJUST_RATIO * timeScale);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return itemList.toString();
    }
}
