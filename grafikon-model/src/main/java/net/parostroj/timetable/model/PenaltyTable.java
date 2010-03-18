package net.parostroj.timetable.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

/**
 * Penalty table.
 *
 * @author jub
 */
public class PenaltyTable implements ObjectWithId {

    private String id;
    private Map<TrainTypeCategory, List<PenaltyTableRow>> rowsMap;
    private List<TrainTypeCategory> categories;

    public PenaltyTable(String id) {
        rowsMap = new HashMap<TrainTypeCategory, List<PenaltyTableRow>>();
        categories = new ArrayList<TrainTypeCategory>();
        this.id = id;
    }

    public void addRowForCategory(TrainTypeCategory category, PenaltyTableRow row) {
        List<PenaltyTableRow> rows = rowsMap.get(category);
        if (rows == null) {
            throw new IllegalStateException("Category doesn't exist.");
        }
        ListIterator<PenaltyTableRow> i = rows.listIterator();
        while (i.hasNext()) {
            PenaltyTableRow currentRow = i.next();
            if (row.getSpeed() < currentRow.getSpeed()) {
                i.previous();
                i.add(row);
                return;
            }
        }
        rows.add(row);
    }

    public void removeRowForSpeedAndCategory(TrainTypeCategory category, int speed) {
        List<PenaltyTableRow> rows = rowsMap.get(category);
        if (rows != null)
            for (Iterator<PenaltyTableRow> i = rows.iterator(); i.hasNext();) {
                PenaltyTableRow row = i.next();
                if (row.getSpeed() == speed) {
                    i.remove();
                }
            }
    }

    public void removeRowForCategory(TrainTypeCategory category, int position) {
        List<PenaltyTableRow> rows = rowsMap.get(category);
        if (rows != null)
            rows.remove(position);
    }

    public PenaltyTableRow getRowForSpeedAndCategory(TrainTypeCategory category, int speed) {
        // zero row is special case
        if (speed == 0)
            return PenaltyTableRow.ZERO_ROW;
        // other rows
        List<PenaltyTableRow> rows = rowsMap.get(category);
        if (rows != null) {
            ListIterator<PenaltyTableRow> i = rows.listIterator();
            while (i.hasNext()) {
                PenaltyTableRow row = i.next();
                if (speed <= row.getSpeed()) {
                    return row;
                }
            }
        }
        // otherwise return null
        return null;
    }

    public PenaltyTableRow getRowForSpeedExactAndCategory(TrainTypeCategory category, int speed) {
        List<PenaltyTableRow> rows = rowsMap.get(category);
        if (rows != null) {
            ListIterator<PenaltyTableRow> i = rows.listIterator();
            while (i.hasNext()) {
                PenaltyTableRow row = i.next();
                if (speed == row.getSpeed()) {
                    return row;
                }
            }
        }
        return null;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TrainTypeCategory getTrainTypeCategoryById(String id) {
        for (TrainTypeCategory category : categories) {
            if (category.getId().equals(id)) {
                return category;
            }
        }
        return null;
    }

    public Set<TrainTypeCategory> getTrainTypeCategoriesByKey(String key) {
        Set<TrainTypeCategory> cs = null;
        for (TrainTypeCategory category : categories) {
            if (category.getKey().equals(key)) {
                if (cs == null)
                    cs = new HashSet<TrainTypeCategory>();
                cs.add(category);
            }
        }
        if (cs == null)
            return Collections.emptySet();
        else
            return cs;
    }

    public void addTrainTypeCategory(TrainTypeCategory category) {
        if (!rowsMap.containsKey(category)) {
            rowsMap.put(category, new LinkedList<PenaltyTableRow>());
            categories.add(category);
        }
    }

    public void addTrainTypeCategory(TrainTypeCategory category, int index) {
        if (!rowsMap.containsKey(category)) {
            rowsMap.put(category, new LinkedList<PenaltyTableRow>());
            categories.add(index, category);
        }
    }

    public void removeTrainTypeCategory(TrainTypeCategory category) {
        rowsMap.remove(category);
        categories.remove(category);
    }

    public void moveTrainTypeCategory(TrainTypeCategory category, int toIndex) {
        if (rowsMap.containsKey(category) && toIndex >= 0 && toIndex < categories.size()) {
            categories.remove(category);
            categories.add(toIndex, category);
        }
    }

    public List<TrainTypeCategory> getTrainTypeCategories() {
        return Collections.unmodifiableList(categories);
    }

    public List<PenaltyTableRow> getPenaltyTableRowsForCategory(TrainTypeCategory category) {
        List<PenaltyTableRow> row = rowsMap.get(category);
        if (row == null)
            return null;
        else
            return Collections.unmodifiableList(row);
    }
}
